if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Schedule = LogicECM.module.WCalendar.Schedule || {};
(function() {
	LogicECM.module.WCalendar.Schedule.scheduleLimitValidation = function Schedule_scheduleLimitValidation(field, args, event, form, silent, message) {
	
		var valid = false;

		var startField, endField;

		startField = document.getElementsByName('prop_lecm-sched_time-limit-start')[0];
		endField = document.getElementsByName('prop_lecm-sched_time-limit-end')[0];
	
		var myID = startField.id;
	
		var IDElements = myID.split("_");
		var myField = IDElements[IDElements.length - 1];
		IDElements.splice(-1, 1);
		var commonID = IDElements.join("_");

		var startValue = startField.value;
		var endValue = endField.value;
	
		if (startValue && endValue) {
			var today = new Date();
			var endDate;
	
			today.setHours(0, 0, 0, 0);
	
			var startDate = new Date(startValue);
			var endDate = new Date(endValue);
	
			showMessage = true;
	
			if (endDate >= startDate) {
				valid = true;
			}
		}

		return valid;
	};
})();

LogicECM.module.WCalendar.Schedule.scheduleLimitValidation.message = function() {
	return Alfresco.util.message('lecm.calendar.start.date.greater');
}