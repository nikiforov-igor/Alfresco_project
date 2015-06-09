(function () {
	var roleId = args['roleId'],
			byChief = false,
			result = false,
			currentEmployee,
			secChiefAssocs,
			depChiefAssocs;

	currentEmployee = orgstructure.getCurrentEmployee();
	model.nodeRef = currentEmployee.nodeRef.toString();
	result = orgstructure.isCurrentEmployeeHasBusinessRole(roleId);

	model.result = result;
	model.byChief = false;
	if (!result) {
		secChiefAssocs = currentEmployee.assocs['lecm-secretary-aspects:chief-assoc'];
		depChiefAssocs = deputyService.getChiefs(currentEmployee);

		if (secChiefAssocs && secChiefAssocs.length > 0) {
			model.result = true;
			model.byChief = true;
		}

		if (depChiefAssocs && depChiefAssocs.length > 0) {
			model.result = true;
			model.byChief = true;
		}
	}


})();