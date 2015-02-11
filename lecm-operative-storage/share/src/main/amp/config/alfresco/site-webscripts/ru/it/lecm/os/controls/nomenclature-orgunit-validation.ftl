<#include "/ru/it/lecm/base-share/components/controls/association-control.ftl">
<#assign dialogId = args.htmlid>
<script type="text/javascript">
	(function(){
		var visible_id = '${dialogId}_assoc_lecm-os_nomenclature-unit-section-unit-assoc-cntrl-currentValueDisplay';
		var errorContainer = '${dialogId}_assoc_lecm-os_nomenclature-unit-section-unit-assoc-cntrl-autocomplete-input'
		var subscribed = false;

		// function createBalloon() {
		// 	return Alfresco.util.createBalloon(errorContainer.parent, {
	 //             effectType: null,
	 //             effectDuration: 0
	 //          });
		// }

		// var tmp = [];
		// tmp.balloon = createBalloon();

		YAHOO.Bubbling.on('mandatoryControlValueUpdated', function(){
			if (!subscribed) {
				var cont = document.getElementById(errorContainer);
				this.balloon = Alfresco.util.createBalloon(cont.parentElement, {
					effectType: null,
					effectDuration: 0
				});
				YAHOO.Bubbling.fire('registerValidationHandler', {
					message: '${msg("lecm.os.msg.nomencl.exists")}',
					fieldId: errorContainer,
					handler: validateHandler.bind(this),
					when: 'onchange'
				});
				subscribed = true;
			}
		});

		function validateHandler(field, args, event, form, silent, message) {
			debugger;
			this.balloon.html(message);
			var valid = false;
			var destination = form.getFormData().alf_destination;
			var orgUnit = form.getFormData()['assoc_lecm-os_nomenclature-unit-section-unit-assoc'];
			if(!orgUnit) {
				return true;
			}
			// Alfresco.util.Ajax.jsonGet(
			// {
			// 	url: Alfresco.constants.PROXY_URI + "/lecm/operative-storage/orgUnitAssociationExists?" + "nodeRef=" + destination + "&orgUnitRef=" + orgUnit,
			// 	successCallback:
			// 	{
			// 		fn: function (response) {
			// 			var oResults = response.json;
			// 			if (oResults != null && !oResults.alreadyExists) {
			// 				valid = true;
			// 			}
			// 		}
			// 	}
			// });

			$.ajax({
				url: Alfresco.constants.PROXY_URI + "/lecm/operative-storage/orgUnitAssociationExists?" + "nodeRef=" + destination + "&orgUnitRef=" + orgUnit,
				context: this,
				success: function (response) {
					debugger;
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

	})();
</script>