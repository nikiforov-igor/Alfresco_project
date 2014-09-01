(function() {
	var documentNodeRef = args['documentNodeRef'];
	var documentNode = search.findNode(documentNodeRef);
	var documentCurrentIterationNode = routesService.getDocumentCurrentIteration(documentNode);
	var approvalState;

	if (documentCurrentIterationNode) {
		model.currentIterationNode = documentCurrentIterationNode.nodeRef.toString();
		approvalState = documentCurrentIterationNode.properties['lecmApproveAspects:approvalState'];
	} else {
		model.currentIterationNode = '';
	}
	model.routeType = routesService.getRouteType();
	model.stageType = routesService.getStageType();
	model.stageItemType = routesService.getStageItemType();
	model.approvalState = approvalState ? approvalState : 'NOT_EXITS';
})();
