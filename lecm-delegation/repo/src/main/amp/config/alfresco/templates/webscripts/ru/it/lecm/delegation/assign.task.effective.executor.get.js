var employeeRef = args["nodeRef"];
var businessRole = args["businessRole"];
var taskID = args["taskID"];
var success = false;

if (employeeRef && businessRole && taskID) {
	var targetEmployee = delegation.assignTaskToEffectiveExecutor(employeeRef, businessRole, taskID);
	if (targetEmployee != null) {
		model.targetEmployee = targetEmployee;
		success = true;
	}
}

model.success = success;
