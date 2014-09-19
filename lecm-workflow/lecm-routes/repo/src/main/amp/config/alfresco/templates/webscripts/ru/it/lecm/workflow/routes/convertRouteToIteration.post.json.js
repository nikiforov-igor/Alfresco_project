(function() {
	var documentNodeStr = json.getString('alf_destination');
	var documentNode = search.findNode(documentNodeStr);
	var routeAssocName = 'assoc_lecmWorkflowRoutes_selectRouteAssoc';
	var routeNode, result, iterationNodeStr = "";
	var stageItems = [], scriptErrors = [];


	if (json.has(routeAssocName)) {
		routeNode = search.findNode(json.getString(routeAssocName));
		result = routesService.convertRouteToIteration(documentNode, routeNode);
		if (result) {
			if (result.iterationNode) {
				iterationNodeStr = result.iterationNode.nodeRef.toString();
			}
			if (result.stageItems) {
				stageItems = result.stageItems;
			}
			if (result.scriptErrors) {
				scriptErrors = result.scriptErrors;
			}
		}
	} else {
		logger.log('Error processing request');
	}

	model.iterationNode = iterationNodeStr;
	model.stageItems = stageItems;
	model.scriptErrors = scriptErrors;
})();
