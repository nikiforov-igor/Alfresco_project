(function() {
	var documentNodeRef = args['documentNodeRef'];
	var documentNode = search.findNode(documentNodeRef);
	var currentIterationNode = routesService.getDocumentCurrentIteration(documentNode);
	var approvalState, approvalStateProp, currentIterationNodeStr, approvalHistoryFolder, approvalHistoryFolderStr = '';
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

	try {
		approvalHistoryFolder = approval.getDocumentApprovalHistoryFolder(documentNode);
	} catch (ex) {
		logger.warn(ex.javaException.getMessage());
	}

	if (approvalHistoryFolder) {
		completedApprovalsCount = approvalHistoryFolder.children.length;
		approvalHistoryFolderStr = approvalHistoryFolder.nodeRef.toString();
	}

	model.routeType = routesService.getRouteType();
	model.stageType = routesService.getStageType();
	model.stageItemType = routesService.getStageItemType();
	model.currentIterationNode = currentIterationNodeStr;
	model.approvalState = approvalState;
	model.completedApprovalsCount = completedApprovalsCount;
	model.sourceRouteInfo = sourceRouteInfo;
	model.approvalIsEditable = approvalIsEditable;
	model.approvalHistoryFolder = approvalHistoryFolderStr;
})();
