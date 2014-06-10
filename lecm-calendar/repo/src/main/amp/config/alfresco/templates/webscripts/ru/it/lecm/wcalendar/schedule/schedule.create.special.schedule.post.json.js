var specialSchedule = schedule.createNewSpecialSchedule(json);

if (specialSchedule != null) {
	model.nodeRef = specialSchedule.nodeRef.toString();
}
