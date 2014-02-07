var businessRoleId = args["businessRoleId"],
    withDelegation = args["withDelegation"],
	orgElementRef = args["orgElement"],
	employees = orgstructure.getEmployeesByBusinessRoleId(businessRoleId, withDelegation && withDelegation == "true"),
	potentialWorkers = edsGlobalSettings.getPotentialWorkers(businessRoleId, orgElementRef),
	workersMap = {},
	results = new Array();

for each (var worker in potentialWorkers) {
	workersMap[worker.nodeRef] = true;
}
for each (var employee in employees) {
	results.push(
		{
			item: employee,
			selectable: !workersMap[employee]
		});
}

model.results = results;