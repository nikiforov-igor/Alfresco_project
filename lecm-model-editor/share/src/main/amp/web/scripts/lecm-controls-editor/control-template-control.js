if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ControlsEditor = LogicECM.module.ControlsEditor || {};

(function() {
	var Dom = YAHOO.util.Dom,
		Event = YAHOO.util.Event,
		mandatoryTemplate = '<span class="mandatory-indicator">{mandatoryIndicator}</span>',
		descriptionTemplate = '<div class="buttons-div"></div>',
		paramTemplate = '<div class="control textfield editmode">' +
							'<div class="label-div">' +
								'<label for="{paramId}">{paramLocalName}:{paramMandatory}</label>' +
							'</div>' +
							'<div class="container">' +
								'{paramDescription}' +
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
			mandatoryIndicator: null,
			selectedValue: null
		},
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

		_createOptionElement: function(controlTemplate) {
			var optionElement = document.createElement('option');
			optionElement.innerHTML = controlTemplate.displayName ? controlTemplate.displayName : controlTemplate.id;
			if (this.options.selectedValue && this.options.selectedValue == controlTemplate.templatePath) {
				optionElement.selected = true;
			}
			optionElement.value = controlTemplate.templatePath;
			return optionElement;
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
			var i, param, mandatoryHTML, descriptionHTML,
				paramsHTML = '',
				configHidden = document.getElementById(this.id + '-control-config-hidden'),
				paramsContainer = document.getElementById(this.id + '-params'),
				selectElement = document.getElementById(this.id),
				selectTabIndex = selectElement.tabIndex;
				selectedValue = selectElement.value,
				controlConfig = this.config[selectedValue],
				controlParams = controlConfig.params;

			configHidden.value = JSON.stringify(controlConfig);

			this._clearAll(paramsContainer);

			for (i in controlParams) {
				param = controlParams[i];
				mandatoryHTML = param.mandatory ? YAHOO.lang.substitute(mandatoryTemplate, {mandatoryIndicator: this.options.mandatoryIndicator}) : '';
				descriptionHTML = descriptionTemplate; //временно, потом будем генерить описание параметра и показывать
				paramsHTML += YAHOO.lang.substitute(paramTemplate, {
					paramId: this.id + '-' + param.id,
					paramLocalName: param.localName ? param.localName : param.id,
					paramMandatory: mandatoryHTML,
					paramDescription: descriptionHTML,
					paramName: param.id,
					paramValue: param.value ? param.value : '',
					paramTabIndex: ++selectTabIndex
				});
			}
			paramsContainer.innerHTML = paramsHTML;
		},

		onReady: function() {
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
			// сгенерировать поля, которые являются параметрами контрола
			// подписать контрол на событие onSelect
			// чтобы на сервак улетали заполненные параметры контрола (наверное в виде json) hidden-поле в котором этот json будет собираться или как-то так
			//кастомный датасурс который штатно сохранит control и кастомно сохранит параметры контрола
		}
	}, true);
})();
