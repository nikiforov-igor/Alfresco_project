var specialShedule = shedule.createNewSpecialShedule(json);

if (specialShedule != null) {
	model.nodeRef = specialShedule.nodeRef.toString();
}
