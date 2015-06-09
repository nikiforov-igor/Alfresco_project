(function () {
	var container = orgstructure.getEmployeesDirectory();
	var itemType = 'lecm-orgstr:employee';
	var currentEmployee = orgstructure.getCurrentEmployee();
	var isEngineer = false;
	var isBoss = false;
	if (currentEmployee != null) {
		isEngineer = orgstructure.isDelegationEngineer(currentEmployee.nodeRef.toString());
		isBoss = orgstructure.isBoss(currentEmployee.nodeRef.toString(), true);
	} else {
		logger.log("ERROR: there is no employee for user " + person.name);
	}

	model.nodeRef = container.nodeRef.toString();
	logger.log("model.nodeRef = " + model.nodeRef);
	model.itemType = itemType;
	logger.log("model.itemType = " + model.itemType);
	model.isEngineer = isEngineer;
	logger.log("model.isEngineer = " + model.isEngineer);
	model.isBoss = isBoss;
	logger.log("model.isBoss = " + model.isBoss);
})();