if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Calendar = LogicECM.module.WCalendar.Calendar || {};
LogicECM.module.WCalendar.Calendar.SpecialDays = LogicECM.module.WCalendar.Calendar.SpecialDays || {};

LogicECM.module.WCalendar.Calendar.SpecialDays.dayExistenceValidation = function SpecialDays_dayExistenceValidation(field, args, event, form, silent, message) {
	var valid = true;
	var dataGrids = [];
	var exactFieldsValue = form.getFormData()["prop_lecm-cal_day"];
	if(!exactFieldsValue) return valid;
	var dayDate = Alfresco.util.fromISO8601(exactFieldsValue);
	var dayStr = pad((dayDate.getMonth() + 1), 2) + pad(dayDate.getDate(), 2);

	// Подгрузить два датагрида
	dataGrids.push(LogicECM.module.Base.Util.findComponentByBubblingLabel("LogicECM.module.Base.DataGrid", LogicECM.module.WCalendar.Calendar.WORKING_DAYS_LABEL));
	dataGrids.push(LogicECM.module.Base.Util.findComponentByBubblingLabel("LogicECM.module.Base.DataGrid", LogicECM.module.WCalendar.Calendar.NON_WORKING_DAYS_LABEL));

	var pickerYear = dayDate.getFullYear();
	var datagridYear = dataGrids[0].options.currentYear;
	//Если введен другой год - сообщаем , что он будет проигнорирован.
	if (datagridYear != pickerYear) {
		Alfresco.util.PopupManager.displayMessage({
			text: Alfresco.util.message("lecm.calendar.msg.wrong.year")
		});
	}

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

	return valid;
};

function pad(num, size) {
	var s = num + "";
	while (s.length < size)
		s = "0" + s;
	return s;
}

LogicECM.module.WCalendar.Calendar.SpecialDays.dayExistenceValidation.message = function() {
	return Alfresco.util.message('lecm.calendar.same.date');
}
