(function () {
	function getDecisionDisplayValue(decision) {
		var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
		var dictionaryService = ctx.getBean('dictionaryService');
		var namespaceService = ctx.getBean('namespaceService');
		var fieldQName = Packages.org.alfresco.service.namespace.QName.createQName(approvalDecisionQName, namespaceService);
		var propDefinition = dictionaryService.getProperty(fieldQName);
		var constraint = propDefinition.getConstraints().get(0).getConstraint();

		return constraint.getDisplayLabel(decision, dictionaryService);
	}

	var documentNodeRef = args['documentNodeRef'];
	var documentNode = search.findNode(documentNodeRef);
	var currentIterationNode = routesService.getDocumentCurrentIteration(documentNode), tempFolder;
	var approvalState, approvalStateProp, currentIterationNodeStr, approvalHistoryFolder, approvalHistoryFolderStr = '';
	var completedApprovalsCount = 0, sourceRouteInfo = '', sourceRouteNode, approvalIsEditable = true;
	var approvalDecisionQName = 'lecmApproveAspects:approvalDecision', approvalResult = '', approvalResultTitle = '';

	if (currentIterationNode) {
		currentIterationNodeStr = currentIterationNode.nodeRef.toString();
		approvalStateProp = currentIterationNode.properties['lecmApproveAspects:approvalState'];
		if (approvalStateProp) {
			approvalState = approvalStateProp;
		} else if (currentIterationNode) {
			approvalState = 'NEW';
		}

		approvalResult = currentIterationNode.properties[approvalDecisionQName] ?
			currentIterationNode.properties[approvalDecisionQName] : '';

		if (approvalResult) {
			approvalResultTitle = getDecisionDisplayValue(approvalResult);
		}

		sourceRouteNode = routesService.getSourceRouteForIteration(currentIterationNode);

		if (sourceRouteNode) {
			sourceRouteInfo = currentIterationNode.properties['cm:title'];
		}

		approvalIsEditable = currentIterationNode.properties['lecmWorkflowRoutes:routeEditable'];
	} else {
		tempFolder = userhome.childByNamePath("temp");
		if (!tempFolder) {
			tempFolder = userhome;
		}
		currentIterationNodeStr = tempFolder.nodeRef.toString();
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
	model.approvalResult = approvalResult;
	model.approvalResultTitle = approvalResultTitle;
	model.completedApprovalsCount = completedApprovalsCount;
	model.sourceRouteInfo = sourceRouteInfo;
	model.approvalIsEditable = approvalIsEditable;
	model.approvalHistoryFolder = approvalHistoryFolderStr;
})();
