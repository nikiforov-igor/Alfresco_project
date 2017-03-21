(function () {
	var fromDate = json.get("fromDate");
	var toDate = json.get("toDate");
	var allDay = ("" + json.get("allDay")) == "true";
	var location = json.get("location");
	var jsonMembers = json.has("members") ? json.get("members") : null;
	var event = json.has("event") ? json.get("event") : null;
	var clientTimezoneOffset = json.get("clientTimezoneOffset");
	var serverTimezoneOffset = new Date().getTimezoneOffset();
	var clientServerTimezoneDifference = Math.floor((clientTimezoneOffset - serverTimezoneOffset)/60);
	var members = [];
	var i;
	var start;
	var end;
	var ewsMembers;

	model.locationAvailable = events.checkLocationAvailable(location, event, fromDate, toDate, allDay, clientServerTimezoneDifference);
	model.members = [];

	if (event && !jsonMembers) {
		members = events.getEventMembers(event);
	} else if (jsonMembers) {
		for (i = 0; i < jsonMembers.length(); ++i) {
			members.push(utils.getNodeFromString(jsonMembers.get(i)));
		}
	}

	start = fromDate.slice(0, fromDate.indexOf("T"));
	end = utils.fromISO8601(toDate);
	end.setDate(end.getDate() + 1);
	end = utils.toISO8601(end);
	end = end.slice(0, end.indexOf("T"));
	ewsMembers = ews.getEvents(members, start + "T00:00:00Z", end +  "T00:00:00Z");

	model.members = members.map(function (member) {
		function isBusy (ewsMember) {
			var ewsMemberRef = '' + ewsMember.employee;
			var memberRef = '' + member.nodeRef.toString();
			return (ewsMemberRef == memberRef) && ewsMember.busytime.length;
		}

		var isAvailable = !ewsMembers.some(isBusy);
		return {
			nodeRef: member.nodeRef.toString(),
			name: substitude.formatNodeTitle(member, "{lecm-orgstr:employee-short-name}"),
			available: isAvailable && events.checkMemberAvailable(member, event, fromDate, toDate, allDay)
		};
	});
})();
