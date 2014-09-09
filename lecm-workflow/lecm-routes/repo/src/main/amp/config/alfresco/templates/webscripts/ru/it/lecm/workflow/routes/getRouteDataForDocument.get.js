(function() {
	var documentNodeRef = args['documentNodeRef'];
	var documentNode = search.findNode(documentNodeRef);
	var currentIterationNode = routesService.getDocumentCurrentIteration(documentNode);
	var approvalState, approvalStateProp, currentIterationNodeStr, approvalHistoryFolder;
	var completedApprovalsCount = 0, sourceRouteInfo = 'UNKNOWN', approvalIsEditable = true;

	if (currentIterationNode) {
		currentIterationNodeStr = currentIterationNode.nodeRef.toString();
		approvalStateProp = currentIterationNode.properties['lecmApproveAspects:approvalState'];
		if (approvalStateProp) {
			approvalState = approvalStateProp;
		} else if (currentIterationNode) {
			approvalState = 'NEW';
		}

		approvalIsEditable = currentIterationNode.properties['lecmWorkflowRoutes:routeEditable'];
	} else {
		currentIterationNodeStr = userhome.childByNamePath("temp").nodeRef.toString();
		approvalState = 'NOT_EXITS';
	}

	approvalHistoryFolder = approval.getDocumentApprovalHistoryFolder(documentNode);

	if (approvalHistoryFolder) {
		completedApprovalsCount = approvalHistoryFolder.children.length;
	}

	model.routeType = routesService.getRouteType();
	model.stageType = routesService.getStageType();
	model.stageItemType = routesService.getStageItemType();
	model.currentIterationNode = currentIterationNodeStr;
	model.approvalState = approvalState;
	model.completedApprovalsCount = completedApprovalsCount;
	model.sourceRouteInfo = sourceRouteInfo;
	model.approvalIsEditable = approvalIsEditable;
})();
