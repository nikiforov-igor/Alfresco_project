if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Schedule = LogicECM.module.WCalendar.Schedule || {};

(function(){
	LogicECM.module.WCalendar.Schedule.timeValidation = function time(field, args, event, form, silent, message) {
		if (Alfresco.logger.isDebugEnabled())
			Alfresco.logger.debug("Validating field '" + field.id + "' with custom time validator");
	
		if (!args) {
			args = {};
		}
	
		if (field.value.length < 1) {
			return false;
		}
	
		args.pattern = /^([0-1]\d|2[0-3]):[0-5]\d(:[0-5]\d)?$/;
		args.match = true;
	
		return Alfresco.forms.validation.regexMatch(field, args, event, form, silent, message);
	};
})();