(function() {
	var request = jsonUtils.toObject(json);

	var nodeRefStr = request.nodeRef;
	var startStr = request.start;
	var start = DateFromISO8601(startStr);

	var workingDaysRequired = request.workingDaysRequired;

	var finishDate = workCalendar.getPlannedJobFinish(nodeRefStr, start, workingDaysRequired);

	if (finishDate === null) {
		finishDate = "never";
	}

	model.finishDate = finishDate.toString();
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
