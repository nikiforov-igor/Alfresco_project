var businessRoleId = args["businessRoleId"],
    withDelegation = args["withDelegation"],
	orgElementRef = args["orgElement"],
	employees = orgstructure.getEmployeesByBusinessRoleId(businessRoleId, withDelegation && withDelegation == "true"),
	results = new Array();

for each (var employee in employees) {
	results.push(
		{
			item: employee,
			selectable: true
		});
}

model.results = results;