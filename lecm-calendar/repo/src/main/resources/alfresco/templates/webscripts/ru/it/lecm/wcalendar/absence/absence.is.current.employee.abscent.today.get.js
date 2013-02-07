var currentEmployee = orgstructure.getCurrentEmployee();
if (currentEmployee != null) {
	var isAbscent = absence.isEmployeeAbscentToday(currentEmployee.nodeRef.toString());
	model.isAbscent = isAbscent;
} else {
	logger.log("ERROR: current employee is null!");
	model.isAbscent = false;
}

