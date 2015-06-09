(function() {
	var employee = search.findNode(args['nodeRef']);
		var orgUnit = orgstructure.getPrimaryOrgUnit(employee);
		model.ignoredString = deputyService.getIgnoredStaffListsString(employee);
		model.path = orgUnit.qnamePath;
})();