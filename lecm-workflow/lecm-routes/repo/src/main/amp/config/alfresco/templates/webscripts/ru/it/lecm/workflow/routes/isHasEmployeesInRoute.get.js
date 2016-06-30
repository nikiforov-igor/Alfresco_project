var route = search.findNode(args["routeRef"]);
var doc = search.findNode(args["documentRef"]);
model.isHasEmployees = routesService.hasEmployeesInDocRoute(route) || routesService.hasPotentialEmployeesInRoute(route, doc);
