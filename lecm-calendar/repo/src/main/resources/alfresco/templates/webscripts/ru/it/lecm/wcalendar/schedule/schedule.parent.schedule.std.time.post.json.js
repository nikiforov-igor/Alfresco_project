var parentSchedule = schedule.getParentScheduleNodeRef(json);

if (parentSchedule != null) {
	model.begin = schedule.getScheduleBeginTime(parentSchedule);
	model.end = schedule.getScheduleEndTime(parentSchedule);
	model.type = schedule.getScheduleType(parentSchedule);
	model.nodeRef = parentSchedule.nodeRef.toString();
}
