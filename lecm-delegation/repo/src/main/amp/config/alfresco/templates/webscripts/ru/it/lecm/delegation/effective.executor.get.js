var employeeRef = args["nodeRef"];
var businessRole = args["businessRole"];

if (employeeRef && businessRole) {
	model.effectiveExecutor = delegation.getEffectiveExecutor(employeeRef, businessRole).nodeRef.toString();
} else if (employeeRef) {
	model.effectiveExecutor = delegation.getEffectiveExecutor(employeeRef).nodeRef.toString();
} else {
	model.effectiveExecutor = "null";
}
