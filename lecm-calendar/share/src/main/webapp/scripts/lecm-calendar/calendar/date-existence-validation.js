if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Calendar = LogicECM.module.WCalendar.Calendar || {};
LogicECM.module.WCalendar.Calendar.SpecialDays = LogicECM.module.WCalendar.Calendar.SpecialDays || {};

LogicECM.module.WCalendar.Calendar.SpecialDays.dayExistenceValidation = function SpecialDays_dayExistenceValidation(field, args, event, form, silent, message) {
	var valid = true;
	var dataGrids = [];

	// ID элемента, куда выплевывать сообщение об ошибке
	form.setErrorContainer("error-message-container");
	// Каждый раз очищать <div>, чтобы не было здоровенной простыни из ошибок
	var htmlNode = YAHOO.util.Dom.get(form.errorContainer);
	htmlNode.innerHTML = "";

	var dayDate = Alfresco.util.fromISO8601(field.value);
	var dayStr = pad((dayDate.getMonth() + 1), 2) + pad(dayDate.getDate(), 2);

	// Подгрузить два датагрида
	dataGrids.push(LogicECM.module.Base.Util.findComponentByBubblingLabel("LogicECM.module.Base.DataGrid", LogicECM.module.WCalendar.Calendar.WORKING_DAYS_LABEL));
	dataGrids.push(LogicECM.module.Base.Util.findComponentByBubblingLabel("LogicECM.module.Base.DataGrid", LogicECM.module.WCalendar.Calendar.NON_WORKING_DAYS_LABEL));

	for (var i = 0; i < dataGrids.length; i++) {
		var tableRows = [];
		tableRows = dataGrids[i].widgets.dataTable.getRecordSet().getRecords();
		// Перебираем все строки датагрида
		for (var j = 0; j < tableRows.length; j++) {
			var tableRow = tableRows[j].getData("itemData");
			var value = tableRow["prop_lecm-cal_day"].value;
			if (dayStr == value) {
				valid = false;
				break;
			}
		}

	}

	//Ругнуться, что дата неправильная
	if (!valid && form) {
		form.addError(message, field);
	}

	return valid;
};

function pad(num, size) {
	var s = num + "";
	while (s.length < size)
		s = "0" + s;
	return s;
}
