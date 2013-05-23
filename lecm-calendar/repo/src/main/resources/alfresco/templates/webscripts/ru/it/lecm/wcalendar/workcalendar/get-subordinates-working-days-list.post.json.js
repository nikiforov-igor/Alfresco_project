(function() {
	var request = jsonUtils.toObject(json);
	var currentEmployee = orgstructure.getCurrentEmployee();
	var result = {};
	var startDate = DateFromISO8601(request.startDate);
	logger.log("startDate - " + request.startDate);
	var endDate = DateFromISO8601(request.endDate);
	logger.log("ebdDate - " + request.endDate);

	if (!currentEmployee) {
		logger.log("ERROR: current employee is null!");
	} else {
		var isEngineer = orgstructure.isCalendarEngineer(currentEmployee.nodeRef.toString());
		var isBoss = orgstructure.isBoss(currentEmployee.nodeRef.toString(), true);

		if (!isEngineer && isBoss) {
			var employees = orgstructure.getBossSubordinate(currentEmployee.nodeRef, true);
			result = getEmployeesWorkingDaysList(employees, startDate, endDate);
		} else if (isEngineer) {
			var employees = search.luceneSearch("TYPE:\"lecm-orgstr:employee\" AND NOT (@lecm-dic\\:active:false)");
			result = getEmployeesWorkingDaysList(employees, startDate, endDate);
		}
	}

	model.result = result;

}());

function getEmployeesWorkingDaysList(employees, startDate, endDate) {
	var result = {};
	for (var i = 0; i < employees.length; i++) {
		logger.log(employees[i].nodeRef);
		var employeeName = employees[i].properties["name"];
		var workingDaysList;
		try {
			workingDaysList = workCalendar.getEmployeeWorkindDays(employees[i], startDate, endDate);
		} catch (err) {
			logger.log(err);
			continue;
		}
		result[employeeName] = [];
		for (var j = 0; j < workingDaysList.length(); j++) {
			logger.log("Date - " + workingDaysList.get(j));
			logger.log("toISO - " + DateToISO8601(new Date(workingDaysList.get(j).getTime())));
			result[employeeName].push(DateToISO8601(new Date(workingDaysList.get(j).getTime())));
		}
	}
	return result;
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
