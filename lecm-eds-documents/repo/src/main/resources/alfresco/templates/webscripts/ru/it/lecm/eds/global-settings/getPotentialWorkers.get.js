var businessRoleId = args["businessRole"];
var organizationElementRef = args["organizationElement"];
var workersList = edsGlobalSettings.getPotentialWorkers(businessRoleId, organizationElementRef);
model.employeesList = workersList;
model.employeesCount = workersList ? workersList.length : 0;