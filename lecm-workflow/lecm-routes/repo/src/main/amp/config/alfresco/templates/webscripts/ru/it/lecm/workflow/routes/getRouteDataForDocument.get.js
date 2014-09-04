(function() {
	var documentNodeRef = args['documentNodeRef'];
	var documentNode = search.findNode(documentNodeRef);
	var documentCurrentIterationNode = routesService.getDocumentCurrentIteration(documentNode);
	var approvalState, approvalStateProp, currentIterationNode;

	if (documentCurrentIterationNode) {
		currentIterationNode = documentCurrentIterationNode.nodeRef.toString();
		approvalStateProp = documentCurrentIterationNode.properties['lecmApproveAspects:approvalState'];
		if (approvalStateProp) {
			approvalState = approvalStateProp;
		} else if (currentIterationNode) {
			approvalState = 'NEW';
		}
	} else {
		currentIterationNode = userhome.childByNamePath("temp").nodeRef.toString();
		approvalState = 'NOT_EXITS';
	}

	model.routeType = routesService.getRouteType();
	model.stageType = routesService.getStageType();
	model.stageItemType = routesService.getStageItemType();
	model.currentIterationNode = currentIterationNode;
	model.approvalState = approvalState;
})();
