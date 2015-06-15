if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.OS = LogicECM.module.OS || {};

(function() {

	LogicECM.module.OS.OrgUnitValidator = function(htmlId) {
		LogicECM.module.OS.OrgUnitValidator.superclass.constructor.call(this, "LogicECM.module.OS.OrgUnitValidator", htmlId);
		this.controlId = htmlId;

		return this;
	}

	YAHOO.extend(LogicECM.module.OS.OrgUnitValidator, Alfresco.component.Base, {

		controlId: null,

		balloon: null,

		options: {
			errorContainer: null,
			currentValue: null
		},

		createBalloon: function() {
			this.balloon = Alfresco.util.createBalloon(document.getElementById(this.options.errorContainer), {
					effectType: null,
					effectDuration: 0
				});
		},

		registerValidator: function() {

			this.createBalloon();

			function messageHandler() {
			}

			function validationHandler(field, args, event, form, silent, message) {
				this.balloon.html(message);
				var valid = false;
				var destination = form.getFormData().alf_destination || this.options.nodeRef;
				var orgUnit = form.getFormData()['assoc_lecm-os_nomenclature-unit-section-unit-assoc'];

				if(!orgUnit || orgUnit == this.options.currentValue) {
					return true;
				}
				
				if(!destination || !orgUnit) {
					return true;
				}

				$.ajax({
					url: Alfresco.constants.PROXY_URI + "/lecm/operative-storage/orgUnitAssociationExists?" + "nodeRef=" + destination + "&orgUnitRef=" + orgUnit,
					context: this,
					success: function (response) {
							var oResults = response;
							if (oResults != null && !oResults.alreadyExists) {
								valid = true;
								this.balloon.hide();
							} else {
								valid = false;
								this.balloon.show();
							}
						},
					async: false
				});

				return valid;
			}


			YAHOO.Bubbling.fire('registerValidationHandler', {
				message: 'Выбранный отдел уже существует',
				fieldId: this.controlId,
				handler: validationHandler.bind(this),
				when: 'onchange'
			});
		}

	});



})();