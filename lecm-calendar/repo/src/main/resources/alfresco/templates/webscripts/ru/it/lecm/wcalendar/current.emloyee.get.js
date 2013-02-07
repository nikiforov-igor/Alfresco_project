var currentEmployee = orgstructure.getCurrentEmployee();
if (currentEmployee != null) {
	model.nodeRef = currentEmployee.nodeRef.toString();
} else {
	logger.log("ERROR: current employee is null!");
}