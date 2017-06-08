model.routesContainer = routesService.getRoutesFolder().nodeRef.toString();
model.routeType = routesService.getRouteType();
model.stageType = routesService.getStageType();
model.stageItemType = routesService.getStageItemType();
model.isEngineer = orgstructure.isCurrentEmployeeHasBusinessRole("ROUTE_ENGINEER");