/* global YAHOO, Alfresco */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.DocumentsTemplates = LogicECM.module.DocumentsTemplates || {};

(function () {
	var Bubbling = YAHOO.Bubbling,
		Dom = YAHOO.util.Dom,
		Selector = YAHOO.util.Selector;

	LogicECM.module.DocumentsTemplates.DetailsView = function (containerId, options, messages) {
		LogicECM.module.DocumentsTemplates.DetailsView.superclass.constructor.call(this, 'LogicECM.module.DocumentsTemplates.DetailsView', containerId);
		this.setOptions(options);
		this.setMessages(messages);
		this.detailsViewId = containerId + '-details';
		Bubbling.on('afterFormRuntimeInit', this.onAfterFormRuntimeInit, this);
		Bubbling.on('beforeTemplate', this.onBeforeTemplate, this);
		Bubbling.on('templateCreated', this.onTemplateCreated, this);
		Bubbling.on('templateEdited', this.onTemplateEdited, this);
		Bubbling.on('submitTemplate', this.onTemplateSubmit, this);
		Bubbling.on('validatorRegister', this.onValidatorRegister, this);
		Bubbling.on('validatorUnregister', this.onValidatorUnregister, this);
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.DocumentsTemplates.DetailsView, Alfresco.component.Base, {

		detailsViewId: null,

		options: {
			bubblingLabel: null
		},

		_destroyForms: function () {
			if (this.widgets.formsRuntime) {
				LogicECM.module.Base.Util.formDestructor('destroy', null, {
					moduleId: this.widgets.formsRuntime.formId, force: true
				});
				delete this.widgets.formsRuntime;
			}
		},

		_hasEventInterest: function (obj) {
			if (!this.options.bubblingLabel || !obj || !obj.bubblingLabel) {
				return true;
			} else {
				return this.options.bubblingLabel === obj.bubblingLabel;
			}
		},

		onAfterFormRuntimeInit: function(layer, args) {

			function onSuccessFormSubmit(successResponse) {
				/* this == Alfresco.FormUI */
				var form = Dom.get(this.formsRuntime.formId),
					templateNode = new Alfresco.util.NodeRef(successResponse.json.persistedObject);
				Alfresco.util.PopupManager.displayMessage({
					text: message
				});
				Bubbling.fire(this.options.mode  + 'Node', {
					bubblingLabel: 'documentsTemplatesTreeView',
					nodeRef: templateNode.nodeRef,
					formData: successResponse.config.dataObj
				});

				var orgInputValue = form["assoc_lecm-template_organizationAssoc"] ? form["assoc_lecm-template_organizationAssoc"].value : null;
				LogicECM.module.Base.Util.reInitializeControl(this.formsRuntime.formId.replace("-form", ""), "lecm-template:organizationAssoc", {
					currentValue: orgInputValue,
					defaultValue: orgInputValue
				});

				// хак для замены формы создания на форму редактирования без обновления страницы
				form.attributes.action.nodeValue = Alfresco.constants.PROXY_URI_RELATIVE + 'api/node/' + templateNode.uri + '/formprocessor';
				this.options.mode = 'edit';
			}

			if (!this.widgets.formsRuntime || args[1].eventGroup == this.widgets.formsRuntime.formId) {
				var obj = args[1],
					form = Dom.get(obj.component.id),
					fieldId = Selector.query('input[name="prop_lecm-template_attributes"]', form, true).id,
					message = this.msg('template-details-successfull-form-submit.title');
				switch (obj.component.options.mode) {
					case 'create': case 'edit':
					if (!this.widgets.formsRuntime) {
						// doBeforeFOrmSubmit; doBeforeAjaxRequest;
						this.widgets.formsRuntime = obj.component.formsRuntime;
						this.widgets.formsRuntime.ajaxSubmitHandlers.successCallback.fn = onSuccessFormSubmit;
						this.widgets.formsRuntime.doBeforeAjaxRequest = {
							fn: function(form)
							{
								var propsForRemove = [];
								for (var property in form.dataObj) {
									if (form.dataObj.hasOwnProperty(property) &&
										property.indexOf("prop_lecm-template") != 0 &&
										property.indexOf("assoc_lecm-template") != 0 &&
										property.indexOf("alf_destination") != 0 &&
										property.indexOf("prop_cm") != 0) {
										propsForRemove.push(property);
									}
								}
								for (var i in propsForRemove) {
									delete form.dataObj[propsForRemove[i]];
								}
								return true;
							},
							scope: this
						};
						this.widgets.formsRuntime.applyTabFix();
					} else {
						console.warn('formsRuntime already exists for LogicECM.module.DocumentsTemplates.DetailsView[' + this.id + '] mode ' + obj.component.options.mode);
					}
					break;
					default: throw obj.component.options.mode + ' mode is not implemented for LogicECM.module.DocumentsTemplates.DetailsView';
				}
				Bubbling.fire('registerValidationHandler', {
					fieldId: fieldId,
					handler: this.validateTemplateDataHandler,
					args: {
						scope: this
					},
					message: this.msg('template-details-invalid-attributes.title')
				});
			}
		},

		validateTemplateDataHandler: function (field, args, event, formsRuntime, silent, message) {

			function hasTemplateValue (attribute) {
				return !!attribute.initial.value;
			}

			var dataStr = field.value,
				templateData = dataStr ? JSON.parse(dataStr) : [];

			return templateData.length && templateData.some(hasTemplateValue);
		},

		onBeforeTemplate: function (layer, args) {
			if (this._hasEventInterest(args[1])) {
				this._destroyForms();
			}
		},

		_onTemplateLoaded: function (html, htmlid) {
			var detailsView = Dom.get(this.detailsViewId),
				markupAndScripts = Alfresco.util.Ajax.sanitizeMarkup(html),
				markup = markupAndScripts[0],
				scripts = markupAndScripts[1];

			detailsView.innerHTML = markup;

			// Make sure forms without Share-specific templates render roughly ok
			Dom.addClass(htmlid + '-form', 'bd');

			// Fix Firefox caret issue
			Alfresco.util.caretFix(htmlid + '-form');

			// The panel is created from the HTML returned in the XHR request, not the container
			var dialogDiv = Dom.getFirstChild(detailsView);
			while (dialogDiv && dialogDiv.tagName.toLowerCase() != 'div') {
				dialogDiv = Dom.getNextSibling(dialogDiv);
			}
			// Run the js code from the webscript's <script> elements
			setTimeout(scripts, 0);
		},

		onTemplateCreated: function (layer, args) {
			var obj = args[1];
			if (this._hasEventInterest(obj)) {
				this._onTemplateLoaded(obj.html, obj.htmlid);
			}
		},

		onTemplateEdited: function (layer, args) {
			var obj = args[1];
			if (this._hasEventInterest(obj)) {
				this._onTemplateLoaded(obj.html, obj.htmlid);
			}
		},

		onTemplateSubmit: function (layer, args) {
			var obj = args[1],
				submitEvent;
			if (this._hasEventInterest(obj)) {
				submitEvent = document.createEvent('Event');
				submitEvent.initEvent('submit', true, false);
				this.widgets.formsRuntime._submitInvoked(submitEvent);
			}
		},

		onReady: function () {
			console.log(this.name + '[' + this.id + '] is ready');
		},

		onValidatorRegister: function (layer, args) {
			var obj = args[1];

			if (this._hasEventInterest(obj)) {
				var fieldConstraints = obj.fieldConstraints;
				if (fieldConstraints) {
					fieldConstraints.forEach(function (constraint) {
						Bubbling.fire('registerValidationHandler', YAHOO.lang.merge(constraint, {
							fieldId: obj.htmlId + '_' + constraint.fieldId,
							handler: eval(constraint.handler),
							args: {}
						}));
					});
				}
			}
		},

		onValidatorUnregister: function(layer, args) {
			var obj = args[1];
			if (this._hasEventInterest(obj)) {
				var fieldId = obj.fieldId;
				YAHOO.util.Event.removeListener(fieldId);
				this.widgets.formsRuntime.validations = this.widgets.formsRuntime.validations.filter(function (validation) {
					return fieldId != validation.fieldId;
				});
			}
		}
	}, true);
})();
