function main (){
	var result = [];

	var nodeRef = args["nodeRef"];
	if (nodeRef != null) {
		var document = search.findNode(nodeRef);
		if (document != null) {
			var dynamicRoles = statemachine.getDynamicRoles(document);
			if (dynamicRoles != null && dynamicRoles.length > 0) {
				for (var i = 0; i < dynamicRoles.length; i++) {
					var roleCode = dynamicRoles[i];
					var role = orgstructure.getBusinessRoleByIdentifier(roleCode);
					if (role != null) {
						var roleEmployees = [];
						var employees = lecmPermission.getEmployeesByDynamicRole(document, roleCode);
						if (employees != null && employees.length > 0) {
							for(var j = 0; j < employees.length; j++) {
								var employee = employees[j];
								roleEmployees.push({
									nodeRef: employee.nodeRef.toString(),
									name: substitude.formatNodeTitle(employee, "{lecm-orgstr:employee-last-name} {lecm-orgstr:employee-first-name} {lecm-orgstr:employee-middle-name}")
								});
							}
						}

						result.push({
							id: roleCode,
							name: role.name,
							employees: roleEmployees
						});
					}
				}
			}
		}
	}

	model.dynamicRoles = result;
}

main ();