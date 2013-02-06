currentEpmoyeeStr = orgstructure.getCurrentEmployee().nodeRef.toString();
activeAbsence = absence.getActiveAbsence(currentEpmoyeeStr);
if (activeAbsence != null) {
	absence.setAbsenceEnd(activeAbsence.nodeRef.toString());
}

