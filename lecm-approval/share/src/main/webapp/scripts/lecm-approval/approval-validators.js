if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Approval = LogicECM.module.Approval || {};


LogicECM.module.Approval.noPastDates = function(field, args,  event, form, silent, message) {
	var result;
	var dateStr = field.value;
	var today = new Date();
	today.setHours(0, 0, 0, 0);
	var date = Alfresco.util.fromISO8601(dateStr);

	if (date < today) {
		result = false;
	} else {
		result = true;
	}

	return result;

};

