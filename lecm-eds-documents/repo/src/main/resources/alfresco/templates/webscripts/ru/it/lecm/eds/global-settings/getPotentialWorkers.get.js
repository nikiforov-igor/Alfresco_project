var businessRoleId = args["businessRole"],
	organizationElementRef = args["organizationElement"],
	workersList = edsGlobalSettings.getPotentialWorkers(businessRoleId, organizationElementRef),
	results = new Array();

for each (var worker in workersList) {
	/*
	var primaryOrgUnit = orgstructure.getPrimaryOrgUnit(worker.getNodeRef().toString());
	if (primaryOrgUnit) {
		worker.properties["primaryOrgUnit"] = primaryOrgUnit.getProperties()["name"];
		worker.properties["primaryOrgUnitRef"] = primaryOrgUnit.getNodeRef().toString();
	}
	*/
	results.push(
		{
			item: worker
		});
}
model.employeesList = results;
model.additionalProperties = new Array("primaryOrgUnit", "primaryOrgUnitRef");
model.employeesCount = workersList ? workersList.length : 0;