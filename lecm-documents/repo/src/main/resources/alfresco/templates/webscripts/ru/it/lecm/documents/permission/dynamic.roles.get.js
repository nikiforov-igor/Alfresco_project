var nodeRef = args["nodeRef"];

if (nodeRef != null) {
    var currentEmployee = orgstructure.getCurrentEmployee();
    var doc = search.findNode(nodeRef);
    var result = lecmPermission.getEmployeeRoles(doc, currentEmployee);
    model.result = jsonUtils.toJSONString(result);
} else {
    model.result = jsonUtils.toJSONString([]);
}