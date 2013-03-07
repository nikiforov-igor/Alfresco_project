if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Absence = LogicECM.module.WCalendar.Schedule || {};

LogicECM.module.WCalendar.Schedule.scheduleLimitValidation = function Schedule_scheduleLimitValidation(field, args, event, form, silent, message) {
	// ID элемента, куда выплевывать сообщение об ошибке
	form.setErrorContainer("error-message-container-set");
	// Каждый раз очищать <div>, чтобы не было здоровенной простыни из ошибок
	var errorContainer = YAHOO.util.Dom.get(form.errorContainer);
	errorContainer.innerHTML = "";

	var valid = false;
	var showMessage = false;

	var myID = field.id;

	var IDElements = myID.split("_");
	var myField = IDElements[IDElements.length - 1];
	IDElements.splice(-1, 1);
	var commonID = IDElements.join("_");

	var startField, endField;

	if (myField.toString() == "time-limit-start") {
		startField = field;
		endField = YAHOO.util.Dom.get(commonID + "_time-limit-end");
	} else if (myField.toString() == "time-limit-end") {
		endField = field;
		startField = YAHOO.util.Dom.get(commonID + "_time-limit-start");
	} else {
		return false;
	}

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

	//Ругнуться, что даты неправильные
	if (!valid && showMessage) {
		errorContainer.innerHTML = "";
		form.addError(message, field);
	}

	return valid;
};
