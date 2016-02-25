/* global YAHOO, Alfresco */

if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.DocumentsTemplates = LogicECM.module.DocumentsTemplates || {};

(function () {
	var Bubbling = YAHOO.Bubbling,
		Dom = YAHOO.util.Dom;

	LogicECM.module.DocumentsTemplates.DetailsView = function (containerId, options, messages) {
		LogicECM.module.DocumentsTemplates.DetailsView.superclass.constructor.call(this, 'LogicECM.module.DocumentsTemplates.DetailsView', containerId);
		this.setOptions(options);
		this.setMessages(messages);
		this.detailsViewId = containerId + '-details';
		Bubbling.on('afterFormRuntimeInit', this.onAfterFormRuntimeInit, this);
		Bubbling.on('beforeTemplate', this.onBeforeTemplate, this);
		Bubbling.on('templateCreated', this.onTemplateCreated, this);
		Bubbling.on('templateEdited', this.onTemplateEdited, this);
		return this;
	};

	YAHOO.lang.extend(LogicECM.module.DocumentsTemplates.DetailsView, Alfresco.component.Base, {

		detailsViewId: null,

		// doBeforeFOrmSubmit; doBeforeAjaxRequest;

		/* получаем NodeRef или itemType, загружаем форму и отображаем ее в себе */

		_destroyForms: function () {
			var i;
			for (i in this.widgets) {
				LogicECM.module.Base.Util.formDestructor('destroy', null, {
					moduleId: this.widgets[i].formId, force: true
				});
				delete this.widgets[i];
				this.widgets[i] = null;
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
			var obj = args[1];
			switch (obj.component.options.mode) {
				case 'create':
					if (!this.widgets.createFormRuntime) {
						this.widgets.createFormRuntime = obj.component.formsRuntime;
						this.widgets.createFormRuntime.applyTabFix();
					} else {
						console.warn('createFormRuntime already exists for LogicECM.module.DocumentsTemplates.DetailsView[' + this.id + ']');
					}
					break;
				case 'edit':
					if (!this.widgets.editFormRuntime) {
						this.widgets.editFormRuntime = obj.component.formsRuntime;
						this.widgets.editFormRuntime.applyTabFix();
					} else {
						console.warn('editFormRuntime already exists for LogicECM.module.DocumentsTemplates.DetailsView[' + this.id + ']');
					}
					break;
				default: throw obj.component.options.mode + ' mode is not implemented for LogicECM.module.DocumentsTemplates.DetailsView';
			}
			// this.widgets.createFormRuntime = new Alfresco.forms.Form(htmlid + '-form');
			// this.widgets.createFormRuntime.setSubmitElements(this.widgets.okButton);
			// this.widgets.createFormRuntime.setAJAXSubmit(true, {
			//	 successCallback: {
			//		 fn: this.onSuccess,
			//		 scope: this
			//	 },
			//	 failureCallback: {
			//		 fn: this.onFailure,
			//		 scope: this
			//	 }
			// });
			// this.widgets.createFormRuntime.setSubmitAsJSON(true);

			// Initialise the form
			// this.widgets.createFormRuntime.init();

			// We're in a popup, so need the tabbing fix
		},

		onBeforeTemplate: function (layer, args) {
			if (this._hasEventInterest(args[1])) {
				this._destroyForms();
			}
		},

		_onTemplateCreated: function (html, htmlid) {
			var detailsView = Dom.get(this.detailsViewId);
			detailsView.innerHTML = html;

			// Make sure forms without Share-specific templates render roughly ok
			Dom.addClass(htmlid + '-form', 'bd');

			// Fix Firefox caret issue
			Alfresco.util.caretFix(htmlid + '-form');

			// The panel is created from the HTML returned in the XHR request, not the container
			var dialogDiv = Dom.getFirstChild(detailsView);
			while (dialogDiv && dialogDiv.tagName.toLowerCase() != 'div') {
				dialogDiv = Dom.getNextSibling(dialogDiv);
			}
		},

		onTemplateCreated: function (layer, args) {
			var obj = args[1];
			if (this._hasEventInterest(obj)) {
				this._onTemplateCreated(obj.html, obj.htmlid);
			}
		},

		_onTemplateEdited: function (html, htmlid) {

		},

		onTemplateEdited: function (layer, args) {
			//TODO: обработчик события "редактировать шаблон"
			var obj = args[1];
			if (this._hasEventInterest(obj)) {
				this._onTemplateEdited(obj.html, obj.htmlid);
			}
		},

		onReady: function () {
			console.log(this.name + '[' + this.id + '] is ready');
		}
	}, true);
})();
