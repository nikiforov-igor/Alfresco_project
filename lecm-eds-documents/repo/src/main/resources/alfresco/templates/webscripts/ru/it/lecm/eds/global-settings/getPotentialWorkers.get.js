var businessRoleId = args["businessRole"],
	organizationElementRef = args["organizationElement"],
	workersList = edsGlobalSettings.getPotentialWorkers(businessRoleId, organizationElementRef),
	results = new Array();

for each (var worker in workersList) {
	results.push(
		{
			item: worker
		});
}
model.employeesList = results;
model.employeesCount = workersList ? workersList.length : 0;