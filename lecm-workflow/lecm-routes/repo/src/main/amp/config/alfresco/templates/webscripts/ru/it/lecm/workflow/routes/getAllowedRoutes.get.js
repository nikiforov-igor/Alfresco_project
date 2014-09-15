(function() {
	var documentRefStr = args['documentRef'];
	var documentRef = search.findNode(documentRefStr);
	var routeNodes = routesService.getAllowedRoutesForCurrentUser(documentRef);

	model.routeNodes = routeNodes;
})();
