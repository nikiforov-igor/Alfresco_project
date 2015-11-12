(function() {

	var monthNames = {
		m00: msg.get("lecm.calendar.absence.january"),
		m01: msg.get("lecm.calendar.absence.february"),
		m02: msg.get("lecm.calendar.absence.march"),
		m03: msg.get("lecm.calendar.absence.april"),
		m04: msg.get("lecm.calendar.absence.may"),
		m05: msg.get("lecm.calendar.absence.june"),
		m06: msg.get("lecm.calendar.absence.july"),
		m07: msg.get("lecm.calendar.absence.august"),
		m08: msg.get("lecm.calendar.absence.september"),
		m09: msg.get("lecm.calendar.absence.october"),
		m10: msg.get("lecm.calendar.absence.november"),
		m11: msg.get("lecm.calendar.absence.december")
	};

	var today = new Date(), daysNumber = 30, finalDate, result = {}, calendarHeader = {};
	var debug = "";
	today.setHours(0);
	today.setMinutes(0);
	today.setSeconds(0);
	today.setMilliseconds(0);

	finalDate = new Date();
	finalDate.setDate(today.getDate() + daysNumber);

	var reqBody = {
		startDate: DateToISO8601(today),
		endDate: DateToISO8601(finalDate)
	};
	var dataRaw = remote.connect("alfresco").post("/lecm/wcalendar/workCalendar/getSubordinatesWorkingDaysList", jsonUtils.toJSONString(reqBody), "application/json");

	if (dataRaw.status == 200) {
		data = eval("(" + dataRaw + ")");
	} else {
		data = [];
	}

	for (i = 0; i < daysNumber; i++) {
		var month = today.getMonth(),
			monthStr = "y" + today.getYear().toString() + (month < 10 ? "m0" : "m") + month.toString();
		if (!calendarHeader[monthStr]) {
			calendarHeader[monthStr] = [];
		}
		calendarHeader[monthStr].push(today.getDate());


		for (var employeeName in data) {
			if (!result[employeeName]) {
				result[employeeName] = [];
			}
			var employeeWorksToday = false;

			for (var j = 0; j < data[employeeName].length; j++) {
				var workingDay = DateFromISO8601(data[employeeName][j]);
				workingDay.getHours(0);
				workingDay.setMinutes(0);
				workingDay.setSeconds(0);
				workingDay.setMilliseconds(0);
				var d1 = new Date(today.getFullYear(), today.getMonth(), today.getDate());
				var d2 = new Date(workingDay.getFullYear(), workingDay.getMonth(), workingDay.getDate());
				if (d1.valueOf() == d2.valueOf()) {
					employeeWorksToday = true;
					break;
				}
			}
			result[employeeName].push(employeeWorksToday);
		}
		today.setDate(today.getDate() + 1);
	}

	today = new Date();
	model.curMonthConst = today.getMonth();
	model.curYearConst = today.getYear();
	model.monthNames = monthNames;
	model.calendarHeader = calendarHeader;
	model.result = result;
	model.debug = debug;
}());


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
				if (match[1]) {
					match[1]--;
				} // Javascript Date months are 0-based
				if (match[6]) {
					match[6] *= 1000;
				} // Javascript Date expects fractional seconds as milliseconds

				result = new Date(match[0] || 1970, match[1] || 0, match[2] || 1, match[3] || 0, match[4] || 0, match[5] || 0, match[6] || 0);

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


function DateToISO8601() {
	var toISOString = function()
	{
		var _ = function(n) {
			return (n < 10) ? "0" + n : n;
		};

		return function(dateObject, options)
		{
			options = options || {};
			var formattedDate = [];
			var getter = options.zulu ? "getUTC" : "get";
			var date = "";
			if (options.selector != "time")
			{
				var year = dateObject[getter + "FullYear"]();
				date = ["0000".substr((year + "").length) + year, _(dateObject[getter + "Month"]() + 1), _(dateObject[getter + "Date"]())].join('-');
			}
			formattedDate.push(date);
			if (options.selector != "date")
			{
				var time = [_(dateObject[getter + "Hours"]()), _(dateObject[getter + "Minutes"]()), _(dateObject[getter + "Seconds"]())].join(':');
				var millis = dateObject[getter + "Milliseconds"]();
				if (options.milliseconds === undefined || options.milliseconds)
				{
					time += "." + (millis < 100 ? "0" : "") + _(millis);
				}
				if (options.zulu)
				{
					time += "Z";
				}
				else if (options.selector != "time")
				{
					var timezoneOffset = dateObject.getTimezoneOffset();
					var absOffset = Math.abs(timezoneOffset);
					time += (timezoneOffset > 0 ? "-" : "+") +
							_(Math.floor(absOffset / 60)) + ":" + _(absOffset % 60);
				}
				formattedDate.push(time);
			}
			return formattedDate.join('T'); // String
		};
	}();

	return toISOString.apply(arguments.callee, arguments);
}
