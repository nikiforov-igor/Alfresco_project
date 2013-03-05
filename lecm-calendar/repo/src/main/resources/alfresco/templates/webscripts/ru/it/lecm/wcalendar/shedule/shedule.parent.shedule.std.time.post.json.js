var parentShedule = shedule.getParentSheduleNodeRef(json);

if (parentShedule != null) {
	model.begin = shedule.getSheduleBeginTime(parentShedule);
	model.end = shedule.getSheduleEndTime(parentShedule);
	model.type = shedule.getSheduleType(parentShedule);
}
