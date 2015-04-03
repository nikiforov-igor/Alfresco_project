if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Events = LogicECM.module.Events || {};

var Dom = YAHOO.util.Dom;

LogicECM.module.Events.changeAllDayValidation =
	function (field, args,  event, form, silent, message) {
		if (field.form != null) {
			var fromDate = field.form["prop_lecm-events_from-date"];
			var toDate = field.form["prop_lecm-events_to-date"];

			var formId = form.formId.replace("-form", "");

			var fromTime = Dom.get(fromDate.id + "-cntrl-time");
			var toTime = Dom.get(toDate.id + "-cntrl-time");

			var allDay = field.value == "true";

			if (fromTime != null) {
				Dom.setStyle(fromTime.id, "display", allDay ? "none" : "block");
				Dom.setStyle(fromTime.id + "-format", "display", allDay ? "none" : "block");

				if (allDay && fromTime.value != "00:01") {
					fromTime.value = "00:01";
					YAHOO.Bubbling.fire("handleFieldChange", {
						formId: formId,
						fieldId: "lecm-events:from-date"
					});
				}

				if (!allDay && fromTime.value.length == 0) {
					Dom.addClass(fromTime.id, "invalid");
				}
			}
			if (toTime != null) {
				if (allDay && toTime.value != "23:59") {
					toTime.value = "23:59";
					YAHOO.Bubbling.fire("handleFieldChange", {
						formId: formId,
						fieldId: "lecm-events:to-date"
					});
				}

				Dom.setStyle(toTime.id, "display", allDay ? "none" : "block");
				Dom.setStyle(toTime.id + "-format", "display", allDay ? "none" : "block");
				if (!allDay && toTime.value.length == 0) {
					Dom.addClass(toTime.id, "invalid");
				}
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