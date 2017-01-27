if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ControlsEditor = LogicECM.module.ControlsEditor || {};

(function() {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		ComponentManager = Alfresco.util.ComponentManager,
		mandatoryTemplate = '<span class="mandatory-indicator">{mandatoryIndicator}</span>',
		descriptionTemplate = '<span class="help-icon">' +
									'<img id="{paramId}-help-icon" src="' + Alfresco.constants.URL_RESCONTEXT + 'components/form/images/help.png"/>' +
							'</span>' +
							'<div class="help-text" id="{paramId}-help">{paramDescription}</div>',
		paramTemplate = '<div class="control textfield editmode">' +
							'<div class="label-div">' +
								'<label for="{paramId}">{paramLocalName}:{paramMandatory}</label>' +
							'</div>' +
							'<div class="container">' +
								'<div class="buttons-div">{paramDescription}</div>' +
								'<div class="value-div">' +
									'<input id="{paramId}" type="text" tabindex="{paramTabIndex}" name="param_{paramName}" value="{paramValue}"/>' +
								'</div>' +
							'</div>' +
						'</div>';

	LogicECM.module.ControlsEditor.ControlTemplateControl = function(containerId) {
		return LogicECM.module.ControlsEditor.ControlTemplateControl.superclass.constructor.call(this, 'LogicECM.module.ControlsEditor.ControlTemplateControl', containerId);
	};

	YAHOO.lang.extend(LogicECM.module.ControlsEditor.ControlTemplateControl, Alfresco.component.Base, {

		options: {
			fieldId: null,
			mandatoryIndicator: null,
			selectedValue: null
		},
		dialogId: null,
		simpleDialog: null,
		config: {},

		_clearAll: function(element) {
			while (element.firstChild) {
				element.removeChild(element.firstChild);
			}
		},

		_createOptionEmptyElement: function() {
			var optionElement = document.createElement('option');
			optionElement.select = true;
			return optionElement;
		},

		_centerDialog: function() {
			if (this.simpleDialog) {
				this.simpleDialog.dialog.center();
			}
		},

		_createOptionElement: function(controlTemplate) {
			var optionElement = document.createElement('option');
			optionElement.innerHTML = controlTemplate.displayName ? controlTemplate.displayName : controlTemplate.id;
			if (this.options.selectedValue && this.options.selectedValue == controlTemplate.templatePath) {
				optionElement.selected = true;
			}
			optionElement.value = controlTemplate.templatePath;
			return optionElement;
		},

		_toggleHelpText: function(event, fieldId) {
			Alfresco.util.toggleHelpText(fieldId);
		},

		applyConfig: function() {
			var templatePath,
				selectElement = Dom.get(this.id);

			this._clearAll(selectElement);
			selectElement.appendChild(this._createOptionEmptyElement());

			for (templatePath in this.config) {
				selectElement.appendChild(this._createOptionElement(this.config[templatePath]));
			}
		},

		onChangeSelect: function(event, obj) {
			var i, param, mandatoryHTML, descriptionHTML, paramId, helpId, helpIconId,
				paramsHTML = '',
				configHidden = document.getElementById(this.id + '-control-config-hidden'),
				paramsContainer = document.getElementById(this.id + '-params'),
				selectElement = document.getElementById(this.id),
				selectTabIndex = selectElement.tabIndex,
				selectedValue = selectElement.value,
				controlConfig = this.config[selectedValue],
				controlParams = controlConfig.params;

			configHidden.value = JSON.stringify(controlConfig);

			this._clearAll(paramsContainer);

			for (i in controlParams) {
				param = controlParams[i];
				paramId = this.id + '-' + param.id;
				mandatoryHTML = param.mandatory ?
								YAHOO.lang.substitute(mandatoryTemplate, {
									mandatoryIndicator: this.options.mandatoryIndicator
								}) : '';
				descriptionHTML = param.description ?
								YAHOO.lang.substitute(descriptionTemplate, {
									paramId: paramId,
									paramDescription: param.description
								}) : '';
				paramsHTML += YAHOO.lang.substitute(paramTemplate, {
					paramId: paramId,
					paramLocalName: param.localName ? param.localName : param.id,
					paramMandatory: mandatoryHTML,
					paramDescription: descriptionHTML,
					paramName: param.id,
					paramValue: param.value ? param.value : '',
					paramTabIndex: ++selectTabIndex
				});
			}
			paramsContainer.innerHTML = paramsHTML;

			for (i in controlParams) {
				param = controlParams[i];
				if (param.description) {
					paramId = this.id + '-' + param.id;
					helpIconId = paramId + '-help-icon';
					helpId = paramId + '-help';
					Alfresco.util.useAsButton(helpIconId, this._toggleHelpText, helpId, this);
				}
			}
			Dom.removeClass(paramsContainer.parentNode, 'hidden');
			this._centerDialog();
		},

		onReady: function() {
			this.dialogId = this.id.replace('_' + this.options.fieldId, '');
			if (this.dialogId) {
				this.simpleDialog = ComponentManager.get(this.dialogId);
			}

			Event.on(this.id, 'change', this.onChangeSelect, null, this);

			var url = Alfresco.constants.URL_SERVICECONTEXT + 'lecm/forms/getConfig?action=getControlsTemplates';

			function onSuccess(serverResponse) {
				if (serverResponse.json) {
					this.config = serverResponse.json;
					this.applyConfig();
				}
			}

			Alfresco.util.Ajax.jsonGet({
				url: url,
				successCallback: {
					scope: this,
					fn: onSuccess
				},
				failureMessage: this.msg('message.failure')
			});
		}
	}, true);
})();
