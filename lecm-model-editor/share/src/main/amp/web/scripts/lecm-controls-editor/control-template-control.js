if (typeof LogicECM == 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.ControlsEditor = LogicECM.module.ControlsEditor || {};

(function() {
	var Dom = YAHOO.util.Dom;

	LogicECM.module.ControlsEditor.ControlTemplateControl = function(containerId) {
		return LogicECM.module.ControlsEditor.ControlTemplateControl.superclass.constructor.call(this, 'LogicECM.module.ControlsEditor.ControlTemplateControl', containerId);
	};

	YAHOO.lang.extend(LogicECM.module.ControlsEditor.ControlTemplateControl, Alfresco.component.Base, {

		options: {
			selectedValue: null
		},
		config: {},

		_clearAll: function(selectElement) {
			while (selectElement.firstChild) {
				selectElement.removeChild(selectElement.firstChild);
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

		onReady: function() {
			console.log('LogicECM.module.ControlsEditor.ControlTemplateControl ready!');
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

			//0. смотрим а есть ли у поля уже значение
			//1. делает ajax-запрос и загружает список контролов какие есть
			//2. по списку контролов наполняет select
			//3. для выбранного контрола генерит поля с параметрами
		}
	}, true);
})();
