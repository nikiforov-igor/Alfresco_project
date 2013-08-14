var errandNodeRef = args['errandNodeRef'];

var currentEmployee = orgstructure.getCurrentEmployee();
var errand = search.findNode(errandNodeRef);

var isInitiator = false;
var isExecutor = false;
var isController = false;
if (errand != null && currentEmployee != null) {
	isInitiator = lecmPermission.hasEmployeeDynamicRole(errand, currentEmployee, "BR_INITIATOR");
	isExecutor = lecmPermission.hasEmployeeDynamicRole(errand, currentEmployee, "ERRANDS_EXECUTOR");
	isController = lecmPermission.hasEmployeeDynamicRole(errand, currentEmployee, "ERRANDS_CONTROLLER");
}
model.isInitiator = isInitiator;
model.isExecutor = isExecutor;
model.isController = isController;