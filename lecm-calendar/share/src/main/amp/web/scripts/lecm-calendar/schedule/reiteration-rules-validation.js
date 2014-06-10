if (typeof LogicECM === "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Absence = LogicECM.module.WCalendar.Schedule || {};

(function() {
	LogicECM.module.WCalendar.Schedule.reiterationRulesValidation = function Schedule_reiterationRulesValidation() {
		var valid = true;
		var errorMessage;
	
		var reiterationType = YAHOO.util.Dom.getElementsBy(function(el) {
			return (el.name === 'reiteration-type' && el.checked);
		}, 'input', 'control-buttons', null, null, null, true);
	
	
		var errorContainer = YAHOO.util.Dom.get('reiteration-rules-error-container');
		errorContainer.innerHTML = "";
	
		if (reiterationType.value === "week-days") {
			var daysChecked = YAHOO.util.Dom.getElementsBy(function(el) {
				return (el.type === 'checkbox' && el.checked);
			}, 'input', 'week-days-mode');
			if (daysChecked.length < 1) {
				valid = false;
				errorMessage = Alfresco.component.Base.prototype.msg("message.error.schedule.reiteration-rules-validation.week-days");
			}
	
		} else if (reiterationType.value === "month-days") {
			var monthDays = YAHOO.util.Dom.get('month-days-input');
			if (monthDays.value.length < 1) {
				valid = false;
				errorMessage = Alfresco.component.Base.prototype.msg("message.error.schedule.reiteration-rules-validation.month-days");
			}
		} else if (reiterationType.value === "shift-work") {
			var workingDays = YAHOO.util.Dom.get("working-days");
			var nonWorkingDays = YAHOO.util.Dom.get("non-working-days");
	
			var workingDaysNum = Number(workingDays.value);
			var nonWorkingDaysNum = Number(nonWorkingDays.value);
	
			if (isNaN(workingDaysNum) || isNaN(nonWorkingDaysNum) || workingDaysNum < 1 || nonWorkingDaysNum < 1) {
				valid = false;
				errorMessage = Alfresco.component.Base.prototype.msg("message.error.schedule.reiteration-rules-validation.shift-work");
			}
		} else {
			valid = false;
			errorMessage = Alfresco.component.Base.prototype.msg("message.error.schedule.reiteration-rules-validation.reiteration-type");
		}
	
		if (!valid && errorMessage.length) {
			errorContainer.innerHTML = errorMessage;
		}
	};
})();