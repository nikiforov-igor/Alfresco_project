var parentShedule = shedule.getParentSheduleNodeRef(json);
if (parentShedule != null) {
	model.nodeRef = parentShedule.nodeRef.toString();
}
