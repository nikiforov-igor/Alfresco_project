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

		controlId: null,

		registerValidator: function() {

			function messageHandler() {
				if(LogicECM.Nomenclature.isCentralized) {
					return 'Неверное значение';
				}
				var orgValue = this.getFormData()['assoc_lecm-os_nomenclature-organization-assoc_added'];
				if(!orgValue) {
					return 'Необходимо выбрать организацию';
				}

				return 'Выбранный год уже существует';
			}

			function validationHandler(field, args, event, form, silent, message) {
				var valid = false;
				var orgValue = form.getFormData()['assoc_lecm-os_nomenclature-organization-assoc_added'];

				if(field.value.length != 4 || (!orgValue && !LogicECM.Nomenclature.isCentralized)) {
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
				message: messageHandler,
				fieldId: this.controlId,
				handler: validationHandler,
				when: 'keyup'
			});
		}

	});
})();