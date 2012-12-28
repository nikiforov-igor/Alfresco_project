if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Calendar = LogicECM.module.WCalendar.Calendar || {};
LogicECM.module.WCalendar.Calendar.SpecialDays = LogicECM.module.WCalendar.Calendar.SpecialDays || {};

LogicECM.module.WCalendar.Calendar.SpecialDays.dayExistenceValidation =
	function SpecialDays_dayExistenceValidation(field, args,  event, form, silent, message) {
		var valid = true;
		var dataGrids = [];

		// ID элемента, куда выплевывать сообщение об ошибке
		form.setErrorContainer("error-message-container");
		// Каждый раз очищать <div>, чтобы не было здоровенной простыни из ошибок
		var htmlNode = Dom.get(form.errorContainer);
		htmlNode.innerHTML = "";

		// Привести даты к одному простому формату
		var shortDateInField = Alfresco.util.formatDate(Alfresco.util.fromISO8601(field.value), "d/m")

		// Подгрузить два датагрида
		dataGrids.push(LogicECM.module.WCalendar.Utils.findGridByName("LogicECM.module.Base.DataGrid", LogicECM.module.WCalendar.Calendar.WORKING_DAYS_LABEL));
		dataGrids.push(LogicECM.module.WCalendar.Utils.findGridByName("LogicECM.module.Base.DataGrid", LogicECM.module.WCalendar.Calendar.NON_WORKING_DAYS_LABEL));

		for(var i = 0; i < dataGrids.length; i++) {
			var tableRows = [];
			tableRows = dataGrids[i].widgets.dataTable.getRecordSet().getRecords();
			// Перебираем все строки датагрида
			for (var j = 0; j < tableRows.length; j++) {			
				var tableRow = tableRows[j].getData("itemData");
				var value = tableRow["prop_lecm-cal_day"].value;
				var shortDateInValue = Alfresco.util.formatDate(Alfresco.util.fromISO8601(value), "d/m");
				if (shortDateInField == shortDateInValue) {
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
