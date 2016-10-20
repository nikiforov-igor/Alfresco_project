var route = search.findNode(args["routeRef"]);
var doc = args["documentRef"] != null ? search.findNode(args["documentRef"]) : null;
model.isRouteEmpty = routesService.isRouteEmpty(route, doc);
