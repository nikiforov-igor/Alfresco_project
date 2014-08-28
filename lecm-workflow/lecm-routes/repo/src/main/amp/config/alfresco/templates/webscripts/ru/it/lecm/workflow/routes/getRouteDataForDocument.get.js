(function() {
	var documentNodeRef = args['documentNodeRef'];
	var documentNode = search.findNode(documentNodeRef);
	var documentCurrentIterationNode = routesService.getDocumentCurrentIteration(documentNode);

	if (documentCurrentIterationNode) {
		model.currentIterationNode = documentCurrentIterationNode.nodeRef.toString();
	} else {
		model.currentIterationNode = '';
	}
	model.routeType = routesService.getRouteType();
	model.stageType = routesService.getStageType();
	model.stageItemType = routesService.getStageItemType();
})();
