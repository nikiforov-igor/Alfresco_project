var employeeSchedule = schedule.getScheduleByOrgSubject(json);

if (employeeSchedule != null) {
	model.begin = schedule.getScheduleBeginTime(employeeSchedule);
	model.end = schedule.getScheduleEndTime(employeeSchedule);
	model.type = schedule.getScheduleType(employeeSchedule);
	model.nodeRef = employeeSchedule.nodeRef.toString();
}
