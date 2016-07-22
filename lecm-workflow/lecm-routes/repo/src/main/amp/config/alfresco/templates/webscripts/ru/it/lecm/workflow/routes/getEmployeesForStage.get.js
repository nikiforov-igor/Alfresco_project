var stageNode = search.findNode(args['stage']),
    routeOrganization = args['organization'] ? args['organization'] : null,
    employees = orgstructure.getEmployeesByBusinessRoleId("LECM_APPROVERS", false),
    stageItemsEmployees = stageNode.children.map(function (stageItem) {
        var employeeAssocs = stageItem.assocs['lecmWorkflowRoutes:stageItemEmployeeAssoc'];
        if (employeeAssocs && employeeAssocs.length) {
            return '' + stageItem.assocs['lecmWorkflowRoutes:stageItemEmployeeAssoc'][0].getId();
        }
        return null;
    }).filter(function (stageItem) {
        return stageItem;
    });

var filteredEmployees;

if (stageItemsEmployees) {
    filteredEmployees = employees.filter(function (employee) {
        for (var i in stageItemsEmployees) {
            if ('' + employee.nodeRef.getId() == stageItemsEmployees[i]) {
                return false;
            }
        }
        return true;
    });
} else {
    filteredEmployees = employees;
}

if (routeOrganization != null) {
    model.employees = filteredEmployees.filter(function (employee) {
        var employeeOrganization = employee.properties['lecm-orgstr-aspects:linked-organization-assoc-ref'];
        return employeeOrganization == routeOrganization;
    });
} else {
    model.employees = filteredEmployees;
}