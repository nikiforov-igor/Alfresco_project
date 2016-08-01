(function() {
	var from = args['from'];
	var to = args['to'];
	var loadActions = args['loadActions'] == "true";
	var mode = args['mode'];
	var timeZoneOffset = null;
    if (args['timeZoneOffset']) {
        timeZoneOffset = parseInt(args['timeZoneOffset']);
    }
	var lastCreated = args['lastCreated'];

	model.events = events.getUserEvents(from + "T00:00:00Z", to + "T23:59:59Z", loadActions, mode, timeZoneOffset,lastCreated);
	var nonWorkingDays = [];
	var wCalendarNonWorkingDays = workCalendar.getEmployeeNonWorkindDays(orgstructure.getCurrentEmployee(), utils.fromISO8601(from + "T00:00:00Z"), utils.fromISO8601(to + "T23:59:59Z"));
	if (wCalendarNonWorkingDays != null) {
		for (var i = 0; i < wCalendarNonWorkingDays.length(); i++) {
			var date = wCalendarNonWorkingDays.get(i);
			nonWorkingDays.push(new Date(date.getTime()));
		}
	}
	model.nonWorkingDays = nonWorkingDays;
    model.mode = mode;
}());