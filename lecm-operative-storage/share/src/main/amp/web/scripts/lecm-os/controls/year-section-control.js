if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.OS = LogicECM.module.OS || {};

(function() {
	LogicECM.module.OS.YearSectionControl = function(htmlId) {
		LogicECM.module.OS.YearSectionControl.superclass.constructor.call(this, "LogicECM.module.OS.YearSectionControl", htmlId);
		this.controlId = htmlId;

		YAHOO.Bubbling.on('organizationChanged', function(layer, args) {
			YAHOO.Bubbling.fire('mandatoryControlValueUpdated');
		});

		return this;
	}

	YAHOO.extend(LogicECM.module.OS.YearSectionControl, Alfresco.component.Base, {

		options: {
			currentValue: null,
			yearFieldId: null,
			orgFieldId: null,
			formId: null,
		},

		controlId: null,

		registerValidator: function() {

			function messageHandler() {
				if(LogicECM.Nomenclature.isCentralized) {
					return 'Неверное значение';
				}

				return 'Выбранный год уже существует';
			}

			function validationHandler(field, args, event, form, silent, message) {

				if(this.options.currentValue && field.value == this.options.currentValue) {
					return true;
				}

				return field.value.length == 4;
			}

			function validateUniqness(field, args, event, form, silent, message) {
				var valid = false;
				var orgValue = form.getFormData()[this.options.orgFieldId];

				if(this.options.currentValue && field.value == this.options.currentValue) {
					return true;
				}

				if(!orgValue && !LogicECM.Nomenclature.isCentralized) {
					return true; //ОЧЕНЬ неправильно, но за заполненность поля отвечает другой валидатор
				}

				if(field.value.length != 4 || isNaN(field.value)) {
					return false;
				}

				$.ajax({
					url: Alfresco.constants.PROXY_URI + "lecm/os/nomenclature/isYearUniq?year=" + field.value + "&orgNodeRef=" + orgValue,
					context: this,
					success: function (response) {
							var oResults = response;
							if (oResults != null && oResults.uniq) {
								valid = true;
							}
						},
					async: false
				});

				return valid;
			}

			YAHOO.Bubbling.fire('registerValidationHandler', {
				message: 'Неверное значение',
				fieldId: this.options.formId + '_' + this.options.yearFieldId,
				handler: validationHandler.bind(this),
				when: 'keyup'
			});

			YAHOO.Bubbling.fire('registerValidationHandler', {
				message: 'Выбранный год уже существует',
				fieldId: this.options.formId + '_' + this.options.yearFieldId,
				handler: validateUniqness.bind(this),
				when: 'keyup'
			});

			if(!LogicECM.Nomenclature.isCentralized) {
				YAHOO.Bubbling.fire('registerValidationHandler', {
				message: 'Необходимо выбрать организацию',
				fieldId: this.options.formId + '_' + this.options.orgFieldId,
				handler: Alfresco.forms.validation.mandatory,
				when: 'keyup'
			});
			}
		}

	});
})();