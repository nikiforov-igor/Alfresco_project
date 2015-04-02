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

			var fromTime = Dom.get(fromDate.id + "-cntrl-time");
			var toTime = Dom.get(toDate.id + "-cntrl-time");

			var allDay = field.value == "true";

			if (fromTime != null) {
				Dom.setStyle(fromTime.id, "display", allDay ? "none" : "block");
				Dom.setStyle(fromTime.id + "-format", "display", allDay ? "none" : "block");

				if (!allDay && fromTime.value.length == 0) {
					Dom.addClass(fromTime.id, "invalid");
				}
			}
			if (toTime != null) {
				Dom.setStyle(toTime.id, "display", allDay ? "none" : "block");
				Dom.setStyle(toTime.id + "-format", "display", allDay ? "none" : "block");
				if (!allDay && toTime.value.length == 0) {
					Dom.addClass(toTime.id, "invalid");
				}
			}
		}
		return true;
	};