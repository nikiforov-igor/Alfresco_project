(function() {
	var from = args['from'];
	var to = args['to'];

	model.events = events.getUserEvents(from, to);
	var nonWorkingDays = [];
	var wCalendarNonWorkingDays = workCalendar.getEmployeeNonWorkindDays(orgstructure.getCurrentEmployee(), utils.fromISO8601(from + "T00:00:00Z"), utils.fromISO8601(to + "T00:00:00Z"));
	if (wCalendarNonWorkingDays != null) {
		for (var i = 0; i < wCalendarNonWorkingDays.length(); i++) {
			var date = wCalendarNonWorkingDays.get(i);
			nonWorkingDays.push(new Date(date));
		}
	}
	model.nonWorkingDays = nonWorkingDays;
}());