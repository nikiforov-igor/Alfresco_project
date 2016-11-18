if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Events = LogicECM.module.Events || {};

var Dom = YAHOO.util.Dom;

LogicECM.module.Events.changeAllDayValidation =
	function (field, args,  event, form, silent, message) {
		return LogicECM.module.Events.baseChangeAllDayValidation(field, form, "lecm-events:from-date", "lecm-events:to-date");
	};

LogicECM.module.Events.requestNewTimeChangeAllDayValidation =
	function (field, args,  event, form, silent, message) {
		return LogicECM.module.Events.baseChangeAllDayValidation(field, form, "lecmEventsWf:requestNewTimeFromDate", "lecmEventsWf:requestNewTimeToDate");
	};

LogicECM.module.Events.repeateDateValidation =
	function (field, args,  event, form, silent, message) {
		return LogicECM.module.Events.repeateDateValidationFunction(field, form, "lecm-events:repeatable-start-period", "lecm-events:repeatable-end-period");
	};

LogicECM.module.Events.fromDateValue = null;
LogicECM.module.Events.toDateValue = null;

LogicECM.module.Events.currentFromDateValue = null;
LogicECM.module.Events.currentToDateValue = null;

LogicECM.module.Events.currentEmployeeBeginTime = null;
LogicECM.module.Events.currentEmployeeEndTime = null;

LogicECM.module.Events.baseChangeAllDayValidation = function (field, form, fromDateFieldId, toDateFieldId) {
    if (LogicECM.module.Events.currentEmployeeBeginTime && LogicECM.module.Events.currentEmployeeEndTime) {
        LogicECM.module.Events.doBaseChangeAllDayValidation(field, form, fromDateFieldId, toDateFieldId);
    } else {
        LogicECM.module.Events.loadCurrentEmployeeSchedule(field, form, fromDateFieldId, toDateFieldId);
    }
    return true;
};

LogicECM.module.Events.loadCurrentEmployeeSchedule = function (field, form, fromDateFieldId, toDateFieldId) {
    Alfresco.util.Ajax.jsonGet({
        url: Alfresco.constants.PROXY_URI + "lecm/orgstructure/api/getCurrentEmployee",
        successCallback: {
            fn: function (response) {
                var employee = response.json;

                if (employee && employee.nodeRef) {
                    Alfresco.util.Ajax.jsonPost({
                        url: Alfresco.constants.PROXY_URI_RELATIVE + "/lecm/wcalendar/schedule/get/employeeScheduleStdTime",
						dataObj: {
							nodeRef: employee.nodeRef,
							fromParent: true,
							exclDefault: false
						},
                        successCallback: {
                            fn: function (response) {
                                var result = response.json;
                                if (result) {
                                    if (result.begin) {
                                        LogicECM.module.Events.currentEmployeeBeginTime = result.begin;
                                    } else {
                                        LogicECM.module.Events.currentEmployeeBeginTime = "00:01";
                                    }
                                    if (result.end) {
                                        LogicECM.module.Events.currentEmployeeEndTime = result.end;
                                    } else {
                                        LogicECM.module.Events.currentEmployeeEndTime = "23:59";
                                    }
                                    LogicECM.module.Events.doBaseChangeAllDayValidation(field, form, fromDateFieldId, toDateFieldId);
                                }
                            },
                            scope: this
                        }
                    });
                }
            },
            scope: this
        }
    });
};

LogicECM.module.Events.doBaseChangeAllDayValidation = function (field, form, fromDateFieldId, toDateFieldId) {
	if (field.form != null) {
		var fromDate = field.form["prop_" + fromDateFieldId.replace(":", "_")];
		var toDate = field.form["prop_" + toDateFieldId.replace(":", "_")];

		var formId = form.formId.replace("-form", "");

		var toDateField = Dom.get(toDate.id + "-cntrl-date");
		var fromTime = Dom.get(fromDate.id + "-cntrl-time");
		var toTime = Dom.get(toDate.id + "-cntrl-time");

		var allDay = field.value == "true";
		toDateField.readOnly = !allDay;
		var times = [
			{
				field: fromTime,
				allDayValue: LogicECM.module.Events.currentEmployeeBeginTime,
				savedValue: LogicECM.module.Events.fromDateValue,
				fieldId: fromDateFieldId
			},
			{
				field: toTime,
				allDayValue: LogicECM.module.Events.currentEmployeeEndTime,
				savedValue: LogicECM.module.Events.toDateValue,
				fieldId: toDateFieldId
			}
		];

		times.forEach(function (time) {
			if (time.field != null) {
				Dom.setStyle(time.field.id + "-container", "display", allDay ? "none" : "block");

				var valueChanged = false;

				if (allDay && time.field.value != time.allDayValue) {
					time.savedValue = time.field.value;
					time.field.value = time.allDayValue;
					valueChanged = true;
				} else if (!allDay && time.savedValue && time.field.value != time.savedValue) {
					time.field.value = time.savedValue;
					time.savedValue = null;
					valueChanged = true;
				}

				if (valueChanged) {
					YAHOO.Bubbling.fire("handleFieldChange", {
						formId: formId,
						fieldId: time.fieldId
					});
				}
			}
		});

		if (allDay && times[0].savedValue) {
			LogicECM.module.Events.fromDateValue = times[0].savedValue;
		} else if (!allDay) {
			LogicECM.module.Events.fromDateValue = times[0].savedValue;
		}

		if (allDay && times[1].savedValue) {
			LogicECM.module.Events.toDateValue = times[1].savedValue;
		} else if (!allDay) {
			LogicECM.module.Events.toDateValue = times[1].savedValue;
		}

		var dates = [
			{
				value: Alfresco.util.fromISO8601(fromDate.value),
				previousValue: LogicECM.module.Events.currentFromDateValue
			},
			{
				value: Alfresco.util.fromISO8601(toDate.value),
				previousValue: LogicECM.module.Events.currentToDateValue
			}
		];

		dates.forEach(function (date, i, dates) {
			if (date.previousValue) {
				if (dates[0].value > dates[1].value) {
					var changed = false;

					if (date.value.getMinutes() != date.previousValue.getMinutes() ||
						date.value.getHours() != date.previousValue.getHours()) {
						if (i == 0) {
							dates[1].value = new Date(dates[0].value.getTime() + 1000 * 60 * 60)
						} else {
							dates[1].value = new Date(dates[0].value.getTime() + 1000 * 60 * 15)
						}
						toDateField.value = dates[1].value.toString(Alfresco.util.message("lecm.form.control.date-picker.entry.date.format"));
						toTime.value = dates[1].value.toString(Alfresco.util.message("form.control.date-picker.entry.time.format"));

						changed = true;


					} else if (date.value.getFullYear() != date.previousValue.getFullYear() ||
						date.value.getMonth() != date.previousValue.getMonth() ||
						date.value.getDate() != date.previousValue.getDate()) {

						dates[1].value.setFullYear(dates[0].value.getFullYear());
						dates[1].value.setMonth(dates[0].value.getMonth());
						dates[1].value.setDate(dates[0].value.getDate());

						toDateField.value = dates[1].value.toString(Alfresco.util.message("lecm.form.control.date-picker.entry.date.format"));

						if (dates[1].value.getHours() < dates[0].value.getHours() ||
							(dates[1].value.getHours() == dates[0].value.getHours() && dates[1].value.getMinutes() < dates[0].value.getMinutes())) {

							dates[1].value.setHours(dates[0].value.getHours());
							dates[1].value.setMinutes(dates[0].value.getMinutes());

							toTime.value = dates[1].value.toString(Alfresco.util.message("form.control.date-picker.entry.time.format"));
						}

						changed = true;
					}

					if (changed) {
						YAHOO.Bubbling.fire("handleFieldChange", {
							formId: formId,
							fieldId: toDateFieldId
						});
						return 0;
					}
				}
			}
		});

		LogicECM.module.Events.currentFromDateValue = dates[0].value;
		LogicECM.module.Events.currentToDateValue = dates[1].value;
	}
	return true;
};

LogicECM.module.Events.repeateDateValidationFunction = function (field, form, fromDateFieldId, toDateFieldId) {
	if (field.form) {
		var fromDate = field.form["prop_" + fromDateFieldId.replace(":", "_")];
		var toDate = field.form["prop_" + toDateFieldId.replace(":", "_")];

        var fromDateInput = Dom.get(fromDate.id + "-cntrl-date");
        var toDateInput = Dom.get(toDate.id + "-cntrl-date");

        if (fromDate.value.length > 0 && toDate.value.length > 0) {
            var fromDateTime = Alfresco.util.fromISO8601(fromDate.value);
            var toDateTime = Alfresco.util.fromISO8601(toDate.value);
            if (toDateTime < fromDateTime) {
                Dom.addClass(fromDateInput, "invalid");
                Dom.addClass(toDateInput, "invalid");
            } else {
                Dom.removeClass(fromDateInput, "invalid");
                Dom.removeClass(toDateInput, "invalid");
            }
        }
	}
	return true;
};

LogicECM.module.Events.changeRepeatableValidation =
	function (field, args,  event, form, silent, message) {
		if (field.form != null) {
			var repeatable = field.value == "true";

			var startPeriod = field.form["prop_lecm-events_repeatable-start-period"];
			var endPeriod = field.form["prop_lecm-events_repeatable-end-period"];
			var repeatableRule = field.form["prop_lecm-events_repeatable-rule"];

			if (startPeriod != null) {
				Dom.setStyle(startPeriod.parentNode.parentNode.parentNode, "display", !repeatable ? "none" : "block");
			}
			if (endPeriod != null) {
				Dom.setStyle(endPeriod.parentNode.parentNode.parentNode, "display", !repeatable ? "none" : "block");
			}
			if (repeatableRule != null) {
				Dom.setStyle(repeatableRule.parentNode, "display", !repeatable ? "none" : "block");
			}
		}
		return true;
	};

LogicECM.module.Events.repeatableValidation =
	function (field, args,  event, form, silent, message) {
		if (field.form != null) {
			var repeatable = field.form["prop_lecm-events_repeatable"];
			return repeatable.value == "false" || field.value.length > 0;
		}
		return false;
	};


LogicECM.module.Events.updateLocationDSValidationLastFromDate = null;
LogicECM.module.Events.updateLocationDSValidationLastToDate = null;
LogicECM.module.Events.updateLocationDSValidation =
	function (field, args,  event, form, silent, message) {
		if (field.form != null) {
			var fromDateValueInput = field.form["prop_lecm-events_from-date"];
			var toDateValueInput = field.form["prop_lecm-events_to-date"];

			if (fromDateValueInput && toDateValueInput) {
				var fromDateInput = Dom.get(fromDateValueInput.id + "-cntrl-date");
				var toDateInput = Dom.get(toDateValueInput.id + "-cntrl-date");
				var fromTimeInput = Dom.get(fromDateValueInput.id + "-cntrl-time");
				var toTimeInput = Dom.get(toDateValueInput.id + "-cntrl-time");

				var fromDate = fromDateValueInput.value;
				var toDate = toDateValueInput.value;

				var formId = form.formId.replace("-form", "");

				if (fromDate.length > 0 && toDate.length > 0 &&
					!Dom.hasClass(fromDateInput, "invalid") && !Dom.hasClass(toDateInput, "invalid") &&
					!Dom.hasClass(fromTimeInput, "invalid") && !Dom.hasClass(toTimeInput, "invalid")) {

					var reinitialize = false;
					if (LogicECM.module.Events.updateLocationDSValidationLastFromDate == null ||
						LogicECM.module.Events.updateLocationDSValidationLastFromDate != fromDate){

						LogicECM.module.Events.updateLocationDSValidationLastFromDate = fromDate;
						reinitialize = true;
					}
					if (LogicECM.module.Events.updateLocationDSValidationLastToDate == null ||
						LogicECM.module.Events.updateLocationDSValidationLastToDate != toDate){

						LogicECM.module.Events.updateLocationDSValidationLastToDate = toDate;
						reinitialize = true;
					}

					LogicECM.module.Base.Util.readonlyControl(formId, "lecm-events:location-assoc", false);
					if (reinitialize) {
						LogicECM.module.Base.Util.reInitializeControl(formId, "lecm-events:location-assoc", {
							fromDate: fromDate,
							toDate: toDate
						});
					}
				} else {
					LogicECM.module.Base.Util.readonlyControl(formId, "lecm-events:location-assoc", true);
				}
			}
		}
		return true;
	};
