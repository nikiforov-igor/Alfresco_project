var route = search.findNode(args["routeRef"]);
var doc = args["documentRef"] != null ? search.findNode(args["documentRef"]) : null;
model.isHasEmployees = routesService.hasEmployeesInDocRoute(route) || (doc != null ?  routesService.hasPotentialEmployeesInRoute(route, doc) : false);
