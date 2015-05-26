if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Events = LogicECM.module.Events || {};

var Dom = YAHOO.util.Dom;

LogicECM.module.Events.changeAllDayValidation =
	function (field, args,  event, form, silent, message) {
		return LogicECM.module.Events.baseChangeAllDayValidation(field, form, "lecm-events:from-date", "lecm-events:to-date");
	};

LogicECM.module.Events.requestNewTimeChangeAllDayValidation =
	function (field, args,  event, form, silent, message) {
		return LogicECM.module.Events.baseChangeAllDayValidation(field, form, "lecmEventsWf:requestNewTimeFromDate", "lecmEventsWf:requestNewTimeToDate");
	};

LogicECM.module.Events.repeateDateValidation =
	function (field, args,  event, form, silent, message) {
		return LogicECM.module.Events.repeateDateValidationFunction(field, form, "lecm-events:repeatable-start-period", "lecm-events:repeatable-end-period");
	};

LogicECM.module.Events.fromDateValue = null;
LogicECM.module.Events.toDateValue = null;

LogicECM.module.Events.baseChangeAllDayValidation = function (field, form, fromDateFieldId, toDateFieldId) {
	if (field.form != null) {
		var fromDate = field.form["prop_" + fromDateFieldId.replace(":", "_")];
		var toDate = field.form["prop_" + toDateFieldId.replace(":", "_")];

		var formId = form.formId.replace("-form", "");

		var fromTime = Dom.get(fromDate.id + "-cntrl-time");
		var toTime = Dom.get(toDate.id + "-cntrl-time");

		Dom.removeClass(fromDate.id+ "-cntrl-date", "invalid");
		Dom.removeClass(fromTime.id, "invalid");
		Dom.removeClass(toDate.id + "-cntrl-date", "invalid");
		Dom.removeClass(toTime.id, "invalid");

		var allDay = field.value == "true";

		if (fromTime != null) {
			Dom.setStyle(fromTime.id + "-container", "display", allDay ? "none" : "block");

			if (allDay && fromTime.value != "00:01") {
				LogicECM.module.Events.fromDateValue = fromTime.value;
				fromTime.value = "00:01";
				YAHOO.Bubbling.fire("handleFieldChange", {
					formId: formId,
					fieldId: fromDateFieldId
				});
			} else if (!allDay && LogicECM.module.Events.fromDateValue != null && fromTime.value != LogicECM.module.Events.fromDateValue) {
				fromTime.value = LogicECM.module.Events.fromDateValue;
				YAHOO.Bubbling.fire("handleFieldChange", {
					formId: formId,
					fieldId: fromDateFieldId
				});
				LogicECM.module.Events.fromDateValue = null;
			}

			if (!allDay && fromTime.value.length == 0) {
				Dom.addClass(fromTime.id, "invalid");
			}
		}
		if (toTime != null) {
			if (allDay && toTime.value != "23:59") {
				LogicECM.module.Events.toDateValue = toTime.value;
				toTime.value = "23:59";
				YAHOO.Bubbling.fire("handleFieldChange", {
					formId: formId,
					fieldId: toDateFieldId
				});
			} else if (!allDay && LogicECM.module.Events.toDateValue != null && toTime.value != LogicECM.module.Events.toDateValue) {
				toTime.value = LogicECM.module.Events.toDateValue;
				YAHOO.Bubbling.fire("handleFieldChange", {
					formId: formId,
					fieldId: toDateFieldId
				});
				LogicECM.module.Events.toDateValue = null;
			}

            Dom.setStyle(toTime.id + "-container", "display", allDay ? "none" : "block");
            if (!allDay && toTime.value.length == 0) {
				Dom.addClass(toTime.id, "invalid");
			}
		}

		var fromDateTime = Alfresco.util.fromISO8601(fromDate.value);
		var toDateTime = Alfresco.util.fromISO8601(toDate.value);
		if (toDateTime < fromDateTime) {
			Dom.addClass(fromDate.id+ "-cntrl-date", "invalid");
			Dom.addClass(fromTime.id, "invalid");
			Dom.addClass(toDate.id + "-cntrl-date", "invalid");
			Dom.addClass(toTime.id, "invalid");
		}
	}
	return true;
};

LogicECM.module.Events.repeateDateValidationFunction = function (field, form, fromDateFieldId, toDateFieldId) {
	if (field.form != null) {
		var fromDate = field.form["prop_" + fromDateFieldId.replace(":", "_")];
		var toDate = field.form["prop_" + toDateFieldId.replace(":", "_")];

		Dom.removeClass(fromDate.id+ "-cntrl-date", "invalid");
		Dom.removeClass(toDate.id + "-cntrl-date", "invalid");


		var fromDateTime = Alfresco.util.fromISO8601(fromDate.value);
		var toDateTime = Alfresco.util.fromISO8601(toDate.value);
		if (toDateTime < fromDateTime) {
			Dom.addClass(fromDate.id+ "-cntrl-date", "invalid");
			Dom.addClass(toDate.id + "-cntrl-date", "invalid");
		}
	}
	return true;
};

LogicECM.module.Events.changeRepeatableValidation =
	function (field, args,  event, form, silent, message) {
		if (field.form != null) {
			var repeatable = field.value == "true";

			var startPeriod = field.form["prop_lecm-events_repeatable-start-period"];
			var endPeriod = field.form["prop_lecm-events_repeatable-end-period"];
			var repeatableRule = field.form["prop_lecm-events_repeatable-rule"];

			if (startPeriod != null) {
				Dom.setStyle(startPeriod.parentNode.parentNode.parentNode, "display", !repeatable ? "none" : "block");
			}
			if (endPeriod != null) {
				Dom.setStyle(endPeriod.parentNode.parentNode.parentNode, "display", !repeatable ? "none" : "block");
			}
			if (repeatableRule != null) {
				Dom.setStyle(repeatableRule.parentNode, "display", !repeatable ? "none" : "block");
			}
		}
		return true;
	};

LogicECM.module.Events.repeatableValidation =
	function (field, args,  event, form, silent, message) {
		if (field.form != null) {
			var repeatable = field.form["prop_lecm-events_repeatable"];
			return repeatable.value == "false" || field.value.length > 0
		}
		return false;
	};


LogicECM.module.Events.updateLocationDSValidationLastFromDate = null;
LogicECM.module.Events.updateLocationDSValidationLastToDate = null;
LogicECM.module.Events.updateLocationDSValidation =
	function (field, args,  event, form, silent, message) {
		if (field.form != null) {
			var fromDateValueInput = field.form["prop_lecm-events_from-date"];
			var toDateValueInput = field.form["prop_lecm-events_to-date"];

			var fromDateInput = Dom.get(fromDateValueInput.id + "-cntrl-time");
			var toDateInput = Dom.get(toDateValueInput.id + "-cntrl-time");
			var fromTimeInput = Dom.get(fromDateValueInput.id + "-cntrl-time");
			var toTimeInput = Dom.get(toDateValueInput.id + "-cntrl-time");

			if (fromDateValueInput != null && toDateValueInput != null &&
					fromDateInput != null && toDateInput != null &&
					fromTimeInput != null && toTimeInput != null) {

				var fromDate = fromDateValueInput.value;
				var toDate = toDateValueInput.value;

				var formId = form.formId.replace("-form", "");

				if (fromDate.length > 0 && toDate.length > 0 &&
					!Dom.hasClass(fromDateInput, "invalid") && !Dom.hasClass(toDateInput, "invalid") &&
					!Dom.hasClass(fromTimeInput, "invalid") && !Dom.hasClass(toTimeInput, "invalid")) {

					var reinitialize = false;
					if (LogicECM.module.Events.updateLocationDSValidationLastFromDate == null ||
						LogicECM.module.Events.updateLocationDSValidationLastFromDate != fromDate){

						LogicECM.module.Events.updateLocationDSValidationLastFromDate = fromDate;
						reinitialize = true;
					}
					if (LogicECM.module.Events.updateLocationDSValidationLastToDate == null ||
						LogicECM.module.Events.updateLocationDSValidationLastToDate != toDate){

						LogicECM.module.Events.updateLocationDSValidationLastToDate = toDate;
						reinitialize = true;
					}

					LogicECM.module.Base.Util.enableControl(formId, "lecm-events:location-assoc");
					if (reinitialize) {
						LogicECM.module.Base.Util.reInitializeControl(formId, "lecm-events:location-assoc", {
							fromDate: fromDate,
							toDate: toDate
						});
					}
				} else {
					LogicECM.module.Base.Util.disableControl(formId, "lecm-events:location-assoc");
				}
			}
		}
		return true;
	};