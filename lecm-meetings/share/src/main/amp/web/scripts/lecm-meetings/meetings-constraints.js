(function () {
	if (typeof LogicECM == "undefined" || !LogicECM) {
		LogicECM = {};
	}

	LogicECM.module = LogicECM.module || {};
	LogicECM.module.Meetings = LogicECM.module.Meetings || {};

	var Dom = YAHOO.util.Dom;

	LogicECM.module.Meetings.agendaItemConnectedWorkplaceValidation =
		function (field, args,  event, form, silent, message) {
			if (field.form != null) {
				var newWorplaceInput = field.form["lecm-meetings-ts_new-workspace"];
				var siteInput = field.form["assoc_lecm-meetings-ts_site-assoc"];

				var formId = form.formId.replace("-form", "");
				if (newWorplaceInput.value == "true"){
					var pickerButton = document.getElementById(formId + '_assoc_lecm-meetings-ts_site-assoc-cntrl-tree-picker-button-button');
					var added = document.getElementById(formId + '_assoc_lecm-meetings-ts_site-assoc-cntrl-added');
					var selected = document.getElementById(formId + '_assoc_lecm-meetings-ts_site-assoc-cntrl-selectedItems');
					added.value = "";
					selected.value = "";
					pickerButton.disabled = true;
					//LogicECM.module.Base.Util.disableControl(formId, "lecm-meetings-ts:site-assoc");
				}
				if (newWorplaceInput.value == "false"){
					//LogicECM.module.Base.Util.enableControl(formId, "lecm-meetings-ts:site-assoc");
				}
				
				if (!siteInput.value){
					//LogicECM.module.Base.Util.enableControl(formId, "lecm-meetings-ts:new-workplace");
				}
			}
			return true;
		};
		
		YAHOO.Bubbling.on("formValueChanged", onFormValueChanged);

		function onFormValueChanged(layer, args) {
			
			var obj = args[1];
			if (obj.eventGroup.options.fieldId == 'lecm-meetings-ts:site-assoc') {
				YAHOO.Bubbling.fire("mandatoryControlValueUpdated", Alfresco.util.ComponentManager.find({id:obj.eventGroup.options.formId}));
			}
		}	
})();	