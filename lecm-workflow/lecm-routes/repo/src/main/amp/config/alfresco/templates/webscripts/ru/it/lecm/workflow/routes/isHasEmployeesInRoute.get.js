var doc = search.findNode(args["documentRef"]);
var hasEmployees = routesService.hasEmployeesInRoute(doc);
var hasStatus = !args["status"] || routesService.getApprovalState(doc) == args["status"];

model.isHasEmployees = hasEmployees && hasStatus;
