function main() {
	var fromDate = json.get("fromDate");
	var toDate = json.get("toDate");
	var allDay = json.get("allDay");
	var location = json.get("location");
	var members = json.has("members") ? json.get("members") : null;
	var event = json.has("event") ? json.get("event") : null;

	model.locationAvailable = events.checkLocationAvailable(location, event, fromDate, toDate, allDay == "true");

	var membersResult = [];
	if (members == null && event != null) {
		members = events.getEventMembers(event);
		for (var i = 0; i < members.length; i++) {
			var memberRef = members[i].nodeRef.toString();

			membersResult.push({
				nodeRef: memberRef,
				name: substitude.formatNodeTitle(memberRef, "{lecm-orgstr:employee-short-name}"),
				available: events.checkMemberAvailable(memberRef, event, fromDate, toDate, allDay == "true")
			});
		}
	} else if (members != null) {
		for (var i = 0; i < members.length(); i++) {
			if (members.get(i).length > 0) {
				membersResult.push({
					nodeRef: members.get(i),
					name: substitude.formatNodeTitle(members.get(i), "{lecm-orgstr:employee-short-name}"),
					available: events.checkMemberAvailable(members.get(i), event, fromDate, toDate, allDay == "true")
				});
			}
		}
	}
	model.members = membersResult;
}

main();