(function() {
	var clientTimeOffset = args["timeOffset"];
	var serverTimeOffset = new Date().getTimezoneOffset();
	var timeOffset = serverTimeOffset - clientTimeOffset;

	var monthNames = {
		m0 : "Январь",
		m1 : "Февраль",
		m2 : "Март",
		m3 : "Апрель",
		m4 : "Май",
		m5 : "Июнь",
		m6 : "Июль",
		m7 : "Август",
		m8 : "Сентябрь",
		m9 : "Октябрь",
		m10 : "Ноябрь",
		m11 : "Декабрь"
	};

	var daysAmount = 30;
    model.result = {};

	var absenceContainer = remote.connect("alfresco").get("/lecm/wcalendar/absence/get/container");
	if (absenceContainer.status != 200) {
		return;
	}
	var absenceContainerObj = jsonUtils.toObject(absenceContainer);

	var reqBody = {
		params: {
			parent: absenceContainerObj.nodeRef ,
			itemType: absenceContainerObj.itemType,
			searchConfig: "",
			maxResults: 9999,
			sort: "cm:name|true",
			searchNodes: "",
			fields: "lecm-absence_abscent-employee-assoc,lecm-absence_begin,lecm-absence_end,lecm-absence_unlimited,lecm-absence_abscence-reason-assoc",
			nameSubstituteStrings: ",,,,",
			showInactive: false
		}
	};

	var dataRaw = remote.connect("alfresco").post("/lecm/wcalendar/absence/get/list/admin", jsonUtils.toJSONString(reqBody), "application/json");

	if (dataRaw.status == 200) {
		data = eval("(" + dataRaw + ")");
	} else {
		data = null;
	}

	if (data != null && data.totalRecords > 0) {
		var employeesAbsences = {};
		var reasons = {};
		for (i = 0; i < data.items.length; i++) {
			itemData = data.items[i].itemData;
			abscentEmployeeDV = itemData["assoc_lecm-absence_abscent-employee-assoc"].displayValue;
			absenceReason = itemData["assoc_lecm-absence_abscence-reason-assoc"];

			if (!employeesAbsences[abscentEmployeeDV]) {
				employeesAbsences[abscentEmployeeDV] = [];
			}
			employeesAbsences[abscentEmployeeDV].push({
				begin: addMinutes(DateFromISO8601(itemData["prop_lecm-absence_begin"].value), timeOffset),
				end: addMinutes(DateFromISO8601(itemData["prop_lecm-absence_end"].value), timeOffset),
				reason: absenceReason.displayValue
			});
			reasons[absenceReason.displayValue] = {
				nodeRef: absenceReason.value
			};
		}

		for (key in reasons) {
			var req =  {
				nodeRef: reasons[key].nodeRef
			};
			var responseRaw = remote.connect("alfresco").post("/lecm/wcalendar/absence/get/absenceReasonColor", jsonUtils.toJSONString(req), "application/json");
			if (responseRaw.status == 200) {
				var response = eval("(" + responseRaw + ")");
				reasons[key].color = response.color;
			} else {
				reasons[key].color = "#000000";
			}

		}

		var today = new Date();
		var calendarHeader = {};

		for (i = 0; i < daysAmount; i++) {
			var month = today.getMonth(),
				monthStr = "y" + today.getYear().toString() + "m" + month.toString();
			if (!calendarHeader[monthStr]) {
				calendarHeader[monthStr] = [];
			}
			calendarHeader[monthStr].push(today.getDate());
			today.setDate(today.getDate() + 1);
		}

		var result = {};

		for (employee in employeesAbsences) {
			var employeeHasAbsence = false;

			if (!result[employee]) {
				result[employee] = [];
			}
			today = new Date();

			for (i = 0; i < daysAmount; i++) {
				var currentDay = new Date(today);
				var color = "";
				for (var j = 0; j < employeesAbsences[employee].length; j++) {
					if (checkDayAbsence(currentDay, employeesAbsences[employee][j].begin, employeesAbsences[employee][j].end)) {
						color = reasons[employeesAbsences[employee][j].reason].color;
						employeeHasAbsence = true;
						break;
					}
				}
				result[employee].push(color);
				today.setDate(today.getDate() + 1);
			}
			if (!employeeHasAbsence) {
				delete result[employee];
			}
		}

		today = new Date();
		model.curMonthConst = today.getMonth();
		model.monthNames = monthNames;
		model.calendarHeader = calendarHeader;
		model.curYearConst = today.getYear();
		model.reasons = reasons;
		model.result = result;
	} else {
		model.result = {};
	}
}());

function checkDayAbsence(day, start, end) {
	var absent = false;
	for (var h = 0; h < 24; h++) {
		for (var m = 0; m < 60; m++) {
			day.setHours(h, m, 0, 0);
			if (day > start && day < end ) {
				absent = true;
				break;
			}
		}
	}
	return absent;
}

function DateFromISO8601() {
	var fromISOString = function()
	{

		var isoRegExp = /^(?:(\d{4})(?:-(\d{2})(?:-(\d{2}))?)?)?(?:T(\d{2}):(\d{2})(?::(\d{2})(.\d+)?)?((?:[+-](\d{2}):(\d{2}))|Z)?)?$/;

		return function(formattedString)
		{
			var match = isoRegExp.exec(formattedString);
			var result = null;

			if (match)
			{
				match.shift();
				if (match[1]){
					match[1]--;
				} // Javascript Date months are 0-based
				if (match[6]){
					match[6] *= 1000;
				} // Javascript Date expects fractional seconds as milliseconds

				result = new Date(match[0]||1970, match[1]||0, match[2]||1, match[3]||0, match[4]||0, match[5]||0, match[6]||0);

				var offset = 0;
				var zoneSign = match[7] && match[7].charAt(0);
				if (zoneSign != 'Z')
				{
					offset = ((match[8] || 0) * 60) + (Number(match[9]) || 0);
					if (zoneSign != '-')
					{
						offset *= -1;
					}
				}
				if (zoneSign)
				{
					offset -= result.getTimezoneOffset();
				}
				if (offset)
				{
					result.setTime(result.getTime() + offset * 60000);
				}
			}

			return result; // Date or null
		};
	}();

	return fromISOString.apply(arguments.callee, arguments);
}

function addMinutes(date, minutes) {
	return new Date(date.getTime() + minutes*60000);
}