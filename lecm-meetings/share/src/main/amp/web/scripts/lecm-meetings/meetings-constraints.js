(function () {
	if (typeof LogicECM == "undefined" || !LogicECM) {
		LogicECM = {};
	}

	LogicECM.module = LogicECM.module || {};
	LogicECM.module.Meetings = LogicECM.module.Meetings || {};

	var Dom = YAHOO.util.Dom;

    LogicECM.module.Meetings.fromDateValidation = function (field, args, event, form, silent, message) {
        if (field.value && field.form) {
            var toInput = field.form["prop_lecm-meetings_actual-to-date"];
            if (toInput && toInput.value) {
                var fromDate = Alfresco.util.fromISO8601(field.value);
                var toDate = Alfresco.util.fromISO8601(toInput.value);
                if (fromDate && toDate) {
                    return fromDate <= toDate;
				}
            }
        }
        return true;
    };

    LogicECM.module.Meetings.dateMoreThanCurrentDateValidation = function (field, args, event, form, silent, message) {
        if (field.value) {
			var selectedDate = Alfresco.util.fromISO8601(field.value);
			if (selectedDate) {
                selectedDate.setHours(0,0,0,0);
                var curDate = new Date();
                curDate.setHours(0,0,0,0);
                return selectedDate <= curDate;
			}
        }
        return true;
    };

	LogicECM.module.Meetings.agendaItemConnectedWorkplaceValidation =
		function (field, args,  event, form, silent, message) {
			if (field.form != null) {
				var newWorkplaceInput = field.form["lecm-meetings-ts_new-workspace"];

				var formId = form.formId.replace("-form", "");
				if (newWorkplaceInput.value == "true"){
					LogicECM.module.Base.Util.disableControl(formId, "lecm-meetings-ts:site-assoc");
				}
				if (newWorkplaceInput.value == "false"){
					LogicECM.module.Base.Util.enableControl(formId, "lecm-meetings-ts:site-assoc");
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