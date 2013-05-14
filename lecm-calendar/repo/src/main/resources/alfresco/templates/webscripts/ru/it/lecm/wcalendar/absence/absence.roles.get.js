var currentEmployee = orgstructure.getCurrentEmployee();
if (currentEmployee != null) {
	var isEngineer = orgstructure.isCalendarEngineer(currentEmployee.nodeRef.toString());
	var isBoss = orgstructure.isBoss(currentEmployee.nodeRef.toString(), true);
	model.isEngineer = isEngineer;
	model.isBoss = isBoss;
} else {
	logger.log("ERROR: current employee is null!");
	model.isEngineer = false;
	model.isBoss = false;
}
