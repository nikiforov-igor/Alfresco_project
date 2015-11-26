var currentEmployee = orgstructure.getCurrentEmployee();
if (currentEmployee != null) {
	var primaryUnit = orgstructure.getPrimaryOrgUnit(currentEmployee);
	if (primaryUnit != null && primaryUnit.nodeRef != null) {
		model.nodeRef = primaryUnit.nodeRef.toString();
	}
}