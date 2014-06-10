var nodeRef = args['nodeRef'];
var node = search.findNode(nodeRef);

model.employees = orgstructure.getOrgRoleEmployees(node);