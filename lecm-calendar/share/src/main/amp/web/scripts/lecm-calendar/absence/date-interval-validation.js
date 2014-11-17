if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.WCalendar = LogicECM.module.WCalendar || {};
LogicECM.module.WCalendar.Absence = LogicECM.module.WCalendar.Absence || {};
LogicECM.module.WCalendar.Absence.dateIntervalValidation = {};
LogicECM.module.WCalendar.Absence.dateIntervalValidation.messageValue = {};

LogicECM.module.WCalendar.Absence.dateIntervalValidation =
	function Absence_dateIntervalValidation(field, args,  event, form, silent, message) {

		function setMessage(text) {
			LogicECM.module.WCalendar.Absence.dateIntervalValidation.messageValue = text;
		}

		setMessage('Неверное значение');

		var valid = true;

		var beginField, endField;

		beginField = document.getElementsByName('prop_lecm-absence_begin')[0];
		endField = document.getElementsByName('prop_lecm-absence_end')[0];
		
		var myID = beginField.id;

		var IDElements = myID.split("_");
		var myField = IDElements[IDElements.length - 1];
		IDElements.splice(-1, 1);
		var commonID = IDElements.join("_");
		IDElements.splice(-2, 2);
		var assocEmployeeID1 = IDElements.join("_") + "_assoc_lecm-absence_abscent-employee-assoc-cntrl-added";
		var assocEmployeeID2 = IDElements.join("_") + "_assoc_lecm-absence_abscent-employee-assoc-added";

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


		// var errorMessage = "";

		if (beginValue && (endValue || isUnlimited)) {
			var today = new Date();
			var endDate;
			var todayTruncated = new Date(today);

			todayTruncated.setHours(0, 0, 0, 0);

			var beginDate = new Date(beginValue);

			if (beginDate < todayTruncated) {
				valid = false;
				// YAHOO.Bubbling.fire('fieldInvalid', beginDate);
				// this.message += Alfresco.component.Base.prototype.msg("message.error.absence.date-interval-validation.early-begin") + "<br>";
				setMessage(Alfresco.component.Base.prototype.msg("message.error.absence.date-interval-validation.early-begin"));
			} else if (beginDate.getTime() == todayTruncated.getTime()) {
				beginDate = today;
			}

			if (!isUnlimited) {
				endDate = new Date(endValue);
				if (beginDate > endDate) {
					valid = false;
					// this.message += Alfresco.component.Base.prototype.msg("message.error.absence.date-interval-validation.early-end") + "<br>";
					setMessage(Alfresco.component.Base.prototype.msg("message.error.absence.date-interval-validation.early-end"));
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

				// Yahoo UI не умеет синхронный (блокирующий) AJAX. Придется использовать jQuery
				jQuery.ajax({
					url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/wcalendar/absence/get/isIntervalSuitableForAbsence",
					type: "POST",
					timeout: 30000, // 30 секунд таймаута хватит всем!
					async: false, // ничего не делаем, пока не отработал запром
					dataType: "json",
					contentType: "application/json",
					data: YAHOO.lang.JSON.stringify(optsObj), // jQuery странно кодирует данные. пусть YUI эаймеся преобразованием в JSON
					processData: false, // данные не трогать, не кодировать вообще
					success: function (result, textStatus, jqXHR) {
						if (result != null && !result.isSuitable) {
							// this.message += Alfresco.component.Base.prototype.msg("message.error.absence.date-interval-validation.already-planned") + "<br>";
							setMessage(Alfresco.component.Base.prototype.msg("message.error.absence.date-interval-validation.already-planned"));
							valid = false;
						} else {
							valid = true;
						}
					},
					error: function(jqXHR, textStatus, errorThrown) {
						Alfresco.util.PopupManager.displayMessage({
							text: "ERROR: can not perform field validation"
						});
						valid = false;
					}	
				});

			}
		} else {
			valid = false;
		}

		return valid;
	};

LogicECM.module.WCalendar.Absence.dateIntervalValidation.message = function() {
	return LogicECM.module.WCalendar.Absence.dateIntervalValidation.messageValue || 'Неверное значение';
}
