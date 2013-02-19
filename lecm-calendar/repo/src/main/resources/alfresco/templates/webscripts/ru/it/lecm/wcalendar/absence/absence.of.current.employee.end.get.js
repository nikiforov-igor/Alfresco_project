var currentEmployee = orgstructure.getCurrentEmployee();
if (currentEmployee != null) {
	var currentEpmoyeeStr = currentEmployee.nodeRef.toString();
	activeAbsence = absence.getActiveAbsence(currentEpmoyeeStr);
	if (activeAbsence != null) {
		absence.setAbsenceEnd(activeAbsence.nodeRef.toString());
		absence.setAbsenceUnlimited(activeAbsence.nodeRef.toString(), false);
	}
} else {
	logger.log("ERROR: current employee is null!");
}
