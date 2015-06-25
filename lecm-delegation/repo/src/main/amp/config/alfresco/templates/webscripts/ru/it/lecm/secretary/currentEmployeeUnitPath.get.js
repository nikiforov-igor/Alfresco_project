(function() {
	var employee = search.findNode(args['nodeRef']);
		var orgUnit = orgstructure.getPrimaryOrgUnit(employee);
		var userOrg = orgstructure.getEmployeeOrganization(employee);
		model.userOrg = userOrg != null ? userOrg.nodeRef.toString() : "";
		model.ignoredString = secretaryService.getIgnoredEmployeesString(employee);
		model.path = orgUnit.qnamePath;
})();