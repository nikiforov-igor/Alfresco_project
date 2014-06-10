var potentialRolesMaps = json.get("potentialRolesMaps"),
	employees = new Array(),
	orgElements = new Array();

for (var rolesMap, i=0; i < potentialRolesMaps.length(); i++) {
	rolesMap = potentialRolesMaps.get(i);

	if (rolesMap && rolesMap.get("businessRoleId") && rolesMap.get("employeesMap")) {
		edsGlobalSettings.savePotentialWorkers(rolesMap.get("businessRoleId"),  rolesMap.get("employeesMap"));
	}
}