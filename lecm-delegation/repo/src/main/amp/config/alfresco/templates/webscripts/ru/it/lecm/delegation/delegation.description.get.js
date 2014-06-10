var page = url.templateArgs.page;
logger.log ("page = " + page);
model.page = page;

function getDescriptionList () {
	var container = delegation.getDelegationOptsContainer ();
	var itemType = delegation.getItemType ();
	var currentEmployee = orgstructure.getCurrentEmployee ();
	var isEngineer = false;
	var isBoss = false;
	if (currentEmployee != null) {
		isEngineer = orgstructure.isDelegationEngineer (currentEmployee.nodeRef.toString ());
		isBoss = orgstructure.isBoss (currentEmployee.nodeRef.toString (), true);
	} else {
		logger.log ("ERROR: there is no employee for user " + person.name);
	}

	model.nodeRef = container.nodeRef.toString ();
	logger.log ("model.nodeRef = " + model.nodeRef);
	model.itemType = itemType;
	logger.log ("model.itemType = " + model.itemType);
	model.isEngineer = isEngineer;
	logger.log ("model.isEngineer = " + model.isEngineer);
	model.isBoss = isBoss;
	logger.log ("model.isBoss = " + model.isBoss);
}

function getDescriptionOpts () {
	var nodeRef = args["nodeRef"]
	logger.log ("nodeRef is " + nodeRef);

	if (!nodeRef) {
		nodeRef = person.nodeRef;
	}

	var currentEmployee = orgstructure.getCurrentEmployee ();
	var isEngineer = false;
	var isBoss = false;
	if (currentEmployee != null) {
		isEngineer = orgstructure.isDelegationEngineer (currentEmployee.nodeRef.toString ());
		isBoss = orgstructure.isBoss (currentEmployee.nodeRef.toString (), true);
	} else {
		logger.log ("ERROR: there is no employee for user " + person.name);
	}
	model.isEngineer = isEngineer;
	logger.log ("model.isEngineer = " + model.isEngineer);
	model.isBoss = isBoss;
	logger.log ("model.isBoss = " + model.isBoss);

	model.hasSubordinate = delegation.hasSubordinate (nodeRef);
	var employee = delegation.getEmployee (nodeRef);
	if (employee) {
		model.employee = employee.nodeRef.toString();
	}
}

switch (page) {
	case "list":
		getDescriptionList ();
		break;
	case "opts":
		getDescriptionOpts ();
		break;
	default:
		status.code = 400;
		status.message = '"' + page + '" is unknown page. Valid page is "list" or "opts"';
		status.redirect = true;
		break;
}
