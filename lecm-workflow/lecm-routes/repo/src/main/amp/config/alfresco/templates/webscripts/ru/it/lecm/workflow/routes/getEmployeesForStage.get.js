var stageNode = search.findNode(args['stage']),
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

if (stageItemsEmployees) {
    model.employees = employees.filter(function (employee) {
        for (var i in stageItemsEmployees) {
            if ('' + employee.nodeRef.getId() == stageItemsEmployees[i]) {
                return false;
            }
        }
        return true;
    });
} else {
    model.employees = employees;
}