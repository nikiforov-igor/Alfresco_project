var businessRoleId = args["businessRole"],
	organizationElementRef = args["organizationElement"],
	argsNameSubstituteString = args['nameSubstituteString'],
	argsSelectedItemsNameSubstituteString = args['selectedItemsNameSubstituteString'] ? args['selectedItemsNameSubstituteString'] : argsNameSubstituteString,
	workersList = edsGlobalSettings.getPotentialWorkers(businessRoleId, organizationElementRef),
	results = new Array();

for each (var worker in workersList) {
	results.push(
		{
			item: worker,
			visibleName: argsNameSubstituteString ? substitude.formatNodeTitle(worker, argsNameSubstituteString) : null,
			selectedVisibleName: argsSelectedItemsNameSubstituteString ? substitude.formatNodeTitle(worker, argsSelectedItemsNameSubstituteString) : null
		});
}
model.employeesList = results;
model.employeesCount = workersList ? workersList.length : 0;