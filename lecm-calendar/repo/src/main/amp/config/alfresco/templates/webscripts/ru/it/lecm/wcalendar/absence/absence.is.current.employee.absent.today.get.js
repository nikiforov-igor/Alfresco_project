var currentEmployee = orgstructure.getCurrentEmployee();
if (currentEmployee != null) {
	var isAbsent = absence.isEmployeeAbsentToday(currentEmployee.nodeRef.toString());
	model.isAbsent = isAbsent;
} else {
	logger.log("ERROR: current employee is null!");
	model.isAbsent = false;
}

