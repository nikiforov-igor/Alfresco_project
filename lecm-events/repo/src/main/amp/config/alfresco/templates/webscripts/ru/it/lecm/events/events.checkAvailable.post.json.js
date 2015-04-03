function main() {
	var fromDate = json.get("fromDate");
	var toDate = json.get("toDate");
	var allDay = json.get("allDay");
	var location = json.get("location");

	model.locationAvailable = events.checkLocationAvailable(location, fromDate, toDate, allDay == "true");
}

main();