/* global Alfresco */
if (typeof LogicECM === 'undefined' || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Workflow = LogicECM.module.Workflow || {};

LogicECM.module.Workflow.workflowDueDateValidator = function(field, args, event, form, silent, message) {
	'use strict';

	var today;
	var selectedDate = Alfresco.util.fromISO8601(field.value); // 1970 год для field.value === ''

	// Если введена невалидная строка даты
	if(selectedDate === null) {
		return false;
	}

	today = new Date();

	today.setHours(0);
	today.setMinutes(0);
	today.setSeconds(0);
	today.setMilliseconds(0);

	// Если общий срок бизнес процесса находится в прошлом
	if (selectedDate - today < 0) {
		return false;
	}

	return true;
};

LogicECM.module.Workflow.workflowListValidator = function(field, args, event, form, silent, message) {
	'use strict';

	function isNotAllowed(record) {
		var ASOC_ASSIGNEE_EMPLOYEE_ASSOC = 'assoc_lecm-workflow_assignee-employee-assoc';
		var ref = record.getData('itemData')[ASOC_ASSIGNEE_EMPLOYEE_ASSOC].value;
		return allowed.indexOf(ref) === -1;
	}

	function hasNoTerm(record) {
		var PROP_ASSIGNEE_DAYS_TO_COMPLETE = 'prop_lecm-workflow_assignee-days-to-complete';
		var propAssigneeDaysToComplete = record.getData('itemData')[PROP_ASSIGNEE_DAYS_TO_COMPLETE];
		return (propAssigneeDaysToComplete) ? !isValue(propAssigneeDaysToComplete.value) : false;
	}

	function hasNoDate(record) {
		var ASOC_ASSIGNEE_EMPLOYEE_ASSOC = 'assoc_lecm-workflow_assignee-employee-assoc';
		var value = record.getData('itemData')[ASOC_ASSIGNEE_EMPLOYEE_ASSOC].value;
		return !isValue(value);
	}

	function isNotInRightOrder(record, i, array) {
		if (i === array.length - 1) {
			return false;
		}

		var currDate, nextDate;

		var PROP_DUE_DATE = 'prop_lecm-workflow_assignee-due-date';

		var curr = record.getData('itemData');
		var next = array[i+1].getData('itemData');

		if(next === null || next === undefined) {
			return false;
		}

		currDate = Alfresco.util.fromISO8601(curr[PROP_DUE_DATE].value);
		nextDate = Alfresco.util.fromISO8601(next[PROP_DUE_DATE].value);

		if(YAHOO.widget.DateMath.before(nextDate, currDate)) {
			return true;
		}

		return false;
	}

	function isNotValid(record) {
		var itemDate;

		var PROP_DUE_DATE = 'prop_lecm-workflow_assignee-due-date';

		var calendarDate = calendar.getSelectedDates()[0];
		var item = record.getData('itemData');

		if (item[PROP_DUE_DATE] && item[PROP_DUE_DATE].value) {
			itemDate = Alfresco.util.fromISO8601(item[PROP_DUE_DATE].value);

			if(!isValue(itemDate) || !isValue(calendarDate)) {
				return true;
			}

			calendarDate.setHours(23);
			calendarDate.setMinutes(59);

			if (YAHOO.widget.DateMath.before(calendarDate, itemDate)) {
				return true;
			}

			return false;
		}

		return true;
	}

	var allowed, records;

	var isValue = YAHOO.lang.isValue;

	var control = args.workflowListControl;
	var calendar = control.widgets.calendar;
	var datagrid = control.widgets.datagrid;

	var formItemType = control.options.formItemType.toUpperCase();
	var concurrency = control.options.concurrency.toUpperCase();

	if(!isValue(datagrid)) {
		return false;
	}

	records = datagrid.widgets.dataTable.getRecordSet().getRecords();
	if(records.length === 0) {
		return false;
	}

	allowed = control.options.allowedNodes;
	if(allowed.length > 0 && records.some(isNotAllowed)) {
		return false;
	}

	if(formItemType === 'ROUTE') {
		if(records.some(hasNoTerm)) {
			return false;
		}
	} else {
		if(concurrency === 'SEQUENTIAL') {
			if(records.some(isNotValid)) {
				return false;
			}
			if (records.some(isNotInRightOrder)) {
				return false;
			}
		}
	}

	return true;
};

LogicECM.module.Workflow.routeHasEmployeesValidator =
	function (field) {
		var valid = false;
		var docNodeRef = Alfresco.util.getQueryStringParameter('nodeRef');
		if (field.value) {
			jQuery.ajax({
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/workflow/routes/isHasEmployees?routeRef=" + field.value + "&documentRef=" + docNodeRef,
				type: "GET",
				timeout: 30000,
				async: false,
				dataType: "json",
				contentType: "application/json",
				processData: false,
				success: function (result) {
					valid = result && result.isHasEmployees;
				},
				error: function() {
					Alfresco.util.PopupManager.displayMessage({
						text: this.msg('message.field.validation.failed')
					});
					valid = false;
				}
			});
		}

		return valid;
	};

LogicECM.module.Workflow.routeIsEmptyValidator =
	function (field) {
		var valid = false;
		var docNodeRef = Alfresco.util.getQueryStringParameter('nodeRef');
		if (field.value) {
			jQuery.ajax({
				url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/workflow/routes/isRouteEmpty?routeRef=" + field.value + "&documentRef=" + docNodeRef,
				type: "GET",
				timeout: 30000,
				async: false,
				dataType: "json",
				contentType: "application/json",
				processData: false,
				success: function (result) {
					valid = result && !result.isRouteEmpty;
				},
				error: function() {
					Alfresco.util.PopupManager.displayMessage({
						text: this.msg('message.field.validation.failed')
					});
					valid = false;
				}
			});
		}

		return valid;
	};