var parentSchedule = schedule.getParentScheduleNodeRef(json);
if (parentSchedule != null) {
	model.nodeRef = parentSchedule.nodeRef.toString();
}
