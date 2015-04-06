function main() {
	var fromDate = json.get("fromDate");
	var toDate = json.get("toDate");
	var allDay = json.get("allDay");
	var location = json.get("location");
	var members = json.get("members");

	model.locationAvailable = events.checkLocationAvailable(location, fromDate, toDate, allDay == "true");

	var membersResult = [];
	if (members != null) {
		for (var i = 0; i < members.length(); i++) {
			membersResult.push({
				nodeRef: members.get(i),
				name: substitude.formatNodeTitle(members.get(i), "{lecm-orgstr:employee-short-name}"),
				available: events.checkMemberAvailable(members.get(i), fromDate, toDate, allDay == "true")
			});
		}
	}
	model.members = membersResult;
}

main();