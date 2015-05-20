function main() {
	var meetingRef = args["meetingRef"];
	model.item = meetings.createNewHoldingItem(meetingRef);
}

main();