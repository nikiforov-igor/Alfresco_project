<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">

(function () {
	function getSourceRouteForIteration(currentIteration) {
		var sourceRoute = routesService.getSourceRouteForIteration(currentIteration);
		if (sourceRoute) {
			return getSourceRouteForIteration(sourceRoute);
		} else {
			return currentIteration;
		}
	}

	var documentNodeRef = args['documentNodeRef'];
	var documentNode = search.findNode(documentNodeRef);
	var currentIterationNode = routesService.getDocumentCurrentIteration(documentNode), tempFolder;
	var approvalState, approvalStateProp, currentIterationNodeStr, approvalHistoryFolder, approvalHistoryFolderStr = '';
	var completedHistoryApprovalsCount = 0, completedCurrentApprovalsCount = 0, sourceRouteInfo = '', sourceRouteNode, approvalIsEditable = true;
	var approvalDecisionQName = 'lecmApproveAspects:approvalDecision', approvalResult = '', approvalResultTitle = '';

	if (currentIterationNode) {
		currentIterationNodeStr = currentIterationNode.nodeRef.toString();
		approvalStateProp = currentIterationNode.properties['lecmApproveAspects:approvalState'];
		if (approvalStateProp) {
			approvalState = '' + approvalStateProp;
		} else if (currentIterationNode) {
			approvalState = 'NEW';
		}

		// ALF-4966 если текущая итерация находится в статусе "Завершен", "Пропущен", "Отменен"
		// ее тоже надо учитывать при подсчете общего числа согласований
		if (['COMPLETE', 'SKIPPED' , 'CANCELLED'].indexOf(approvalState) >= 0) {
			completedCurrentApprovalsCount = 1;
		}

		approvalResult = currentIterationNode.properties[approvalDecisionQName] ?
			currentIterationNode.properties[approvalDecisionQName] : '';

		if (approvalResult) {
			var propDefinition = base.getProperty('lecmApproveAspects:approvalDecision');
			approvalResultTitle = Evaluator.translateField(propDefinition, approvalResult);
		}

		sourceRouteNode = getSourceRouteForIteration(currentIterationNode);

		if (routesService.getRoutesFolder().equals(sourceRouteNode.parent)) {
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
		completedHistoryApprovalsCount = approvalHistoryFolder.children.length;
		approvalHistoryFolderStr = approvalHistoryFolder.nodeRef.toString();
	}

	model.routeType = routesService.getRouteType();
	model.stageType = routesService.getStageType();
	model.stageItemType = routesService.getStageItemType();
	model.currentIterationNode = currentIterationNodeStr;
	model.approvalState = approvalState;
	model.approvalResult = approvalResult;
	model.approvalResultTitle = approvalResultTitle;
	model.completedCurrentApprovalsCount = completedCurrentApprovalsCount;
	model.completedHistoryApprovalsCount = completedHistoryApprovalsCount;
	model.sourceRouteInfo = sourceRouteInfo;
	model.approvalIsEditable = approvalIsEditable;
	model.approvalHistoryFolder = approvalHistoryFolderStr;
})();
