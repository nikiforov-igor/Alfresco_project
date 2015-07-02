var rolesId = args['rolesId'];
var hasOneRole = false;
if (rolesId != null) {
	var roles = rolesId.split(",");
	for (var i = 0; i < roles.length; i++) {
		if (orgstructure.isCurrentEmployeeHasBusinessRole(roles[i])) {
			hasOneRole = true;
			break;
		}
	}
}

model.hasRole = hasOneRole;