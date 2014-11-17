if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Subscriptions = LogicECM.module.Subscriptions || {};

LogicECM.module.Subscriptions.destinationValidation =
	function Subscriptions_destinationValidation(field, args,  event, form, silent, message) {
		var employee = field.form["assoc_lecm-subscr_destination-employee-assoc"];
		var organizationUnit = field.form["assoc_lecm-subscr_destination-organization-unit-assoc"];
		var positions = field.form["assoc_lecm-subscr_destination-position-assoc"];
		var workGroups = field.form["assoc_lecm-subscr_destination-work-group-assoc"];
		var businessRoles = field.form["assoc_lecm-subscr_destination-business-role-assoc"];

		return (employee != null && employee.value.length > 0) ||
			(organizationUnit != null && organizationUnit.value.length > 0) ||
			(positions != null && positions.value.length > 0) ||
			(businessRoles != null && businessRoles.value.length > 0) ||
			(workGroups != null && workGroups.value.length > 0);
	};
