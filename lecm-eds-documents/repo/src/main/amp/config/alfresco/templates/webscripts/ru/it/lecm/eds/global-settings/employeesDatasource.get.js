var businessRoleId = args["businessRoleId"],
    withDelegation = args["withDelegation"],
	orgElementRef = args["orgElement"],
	argsNameSubstituteString = args['nameSubstituteString'],
	argsSelectedItemsNameSubstituteString = args['selectedItemsNameSubstituteString'] ? args['selectedItemsNameSubstituteString'] : argsNameSubstituteString,
	employees = orgstructure.getEmployeesByBusinessRoleId(businessRoleId, withDelegation && withDelegation == "true"),
	results = new Array();

    for each(var employee in employees) {
        results.push(
            {
                item: employee,
                selectable: true,
                visibleName: argsNameSubstituteString ? substitude.formatNodeTitle(employee, argsNameSubstituteString) : null,
                selectedVisibleName: argsSelectedItemsNameSubstituteString ? substitude.formatNodeTitle(employee, argsSelectedItemsNameSubstituteString) : null
            });
    }

model.results = results;