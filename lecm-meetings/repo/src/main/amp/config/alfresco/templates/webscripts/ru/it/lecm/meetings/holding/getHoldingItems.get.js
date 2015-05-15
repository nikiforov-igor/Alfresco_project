function main() {
	var meetingRef = args["meetingRef"];
	model.items = meetings.getMeetingHoldingItems(meetingRef);
}

main();