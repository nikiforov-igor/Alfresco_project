if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Absence = LogicECM.module.WCalendar.Absence || {};


LogicECM.module.WCalendar.Absence.dateIntervalValidation =
	function Absence_dateIntervalValidation(field, args,  event, form, silent, message) {
		// ID элемента, куда выплевывать сообщение об ошибке
		form.setErrorContainer("error-message-container-set");
		// Каждый раз очищать <div>, чтобы не было здоровенной простыни из ошибок
		var errorContainer = YAHOO.util.Dom.get(form.errorContainer);
		errorContainer.innerHTML = "";

		if (this.formIsValid) {
			this.formIsValid = false;
			return true;
		}
		var valid = true;

		var myID = field.id;

		var IDElements = myID.split("_");
		var myField = IDElements[IDElements.length - 1];
		IDElements.splice(-1, 1);
		var commonID = IDElements.join("_");
		IDElements.splice(-2, 2);
		var assocEmployeeID1 = IDElements.join("_") + "_assoc_lecm-absence_abscent-employee-assoc-cntrl-added";
		var assocEmployeeID2 = IDElements.join("_") + "_assoc_lecm-absence_abscent-employee-assoc-added";

		var beginField, endField;

		if (myField.toString() == "begin") {
			beginField = field;
			endField = YAHOO.util.Dom.get(commonID + "_end");
		} else if (myField.toString() == "end") {
			endField = field;
			beginField = YAHOO.util.Dom.get(commonID + "_begin");
		} else {
			return false;
		}

		var unlimitedCheckbox = YAHOO.util.Dom.get(commonID + "_unlimited");
		var isUnlimited = unlimitedCheckbox.checked;
		var beginValue = beginField.value;
		var endValue = endField.value;
		var assocEmployeeField = YAHOO.util.Dom.get(assocEmployeeID1);
		if (!assocEmployeeField) {
			assocEmployeeField = YAHOO.util.Dom.get(assocEmployeeID2);
		}
		if (assocEmployeeField) {
			var assocEmployeeRef = assocEmployeeField.value;
		}


		var errorMessage = "";

		if (beginValue && (endValue || isUnlimited)) {
			var today = new Date();
			var endDate;

			today.setHours(0, 0, 0, 0);

			var beginDate = new Date(beginValue);

			if (beginDate < today) {
				valid = false;
				errorMessage += "Начало отсутствия раньше сегодняшней даты. <br>";
			}

			if (!isUnlimited) {
				endDate = new Date(endValue);
				if (beginDate > endDate) {
					valid = false;
					errorMessage += "Конец отсутствия раньше начала. <br>";
				}
			} else {
				endDate = new Date(beginDate);
			}

			if (valid &&  assocEmployeeRef) {
				endDate.setHours(23, 59, 59, 0);
				optsObj = {
					begin: Alfresco.util.toISO8601(beginDate),
					end: Alfresco.util.toISO8601(endDate),
					nodeRef: assocEmployeeRef
				};

				Alfresco.util.Ajax.request({
					method: "POST",
					url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/absence/get/isIntervalSuitableForAbsence",
					dataObj: optsObj,
					requestContentType: "application/json",
					responseContentType: "application/json",
					successCallback: {
						fn: function (response) {
							var result = response.json;

							if (result != null && !result.isSuitable) {
								errorMessage += "На выбранную дату ужа запланировано отсутствие. <br>";
								errorContainer.innerHTML = "";
								form.addError(errorMessage, field);
							} else {
								this.formIsValid = true;
								YAHOO.Bubbling.fire("mandatoryControlValueUpdated", this);
							}
						},
						scope: this
					}
				});
			}
		}

		//Ругнуться, что дата неправильная
		if (!valid) {
			errorContainer.innerHTML = "";
			form.addError(errorMessage, field);
		}

		return false;
	};
