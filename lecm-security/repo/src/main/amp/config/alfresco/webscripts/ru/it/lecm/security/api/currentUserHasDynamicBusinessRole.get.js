var node = search.findNode(args['nodeRef']);
var role = args['role'];
var currentEmployee = orgstructure.getCurrentEmployee();

model.hasDynamicRole = lecmPermission.hasEmployeeDynamicRole(node, currentEmployee, role);