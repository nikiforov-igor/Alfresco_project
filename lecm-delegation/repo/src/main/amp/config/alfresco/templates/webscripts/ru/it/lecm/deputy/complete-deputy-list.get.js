(function() {
	var employeeNodeStr = args['nodeRef'];
	var employeeNodeRef = search.findNode(employeeNodeStr);
	var results = [];

	var orgUnit = orgstructure.getPrimaryOrgUnit(employeeNodeRef);

	var deputyAssocs = employeeNodeRef.assocs['lecm-deputy:deputy-assoc'];
	for each(deputy in deputyAssocs) {
		if(!!deputy.properties['lecm-deputy:complete-deputy-flag']) {
			var employeeAssocs = deputy.assocs['lecm-deputy:employee-assoc'];
			if(employeeAssocs) {
				var employee = employeeAssocs[0];
				results.push(employee.nodeRef.toString());
			}
		}
	}

	var userOrg = orgstructure.getEmployeeOrganization(employeeNodeRef);

	if(!userOrg) {
		model.userOrg = 'NOT_REF'
	} else {
		model.userOrg = userOrg.nodeRef.toString();
	}

	model.ignoredString = deputyService.getIgnoredString(employeeNodeRef);
	model.path = orgUnit.qnamePath;
	model.results = results;
})();