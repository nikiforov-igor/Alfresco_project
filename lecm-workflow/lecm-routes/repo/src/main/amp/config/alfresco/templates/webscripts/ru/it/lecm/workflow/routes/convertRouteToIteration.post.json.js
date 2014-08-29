(function() {
	var UUID = Packages.java.util.UUID;

	var documentNodeStr = json.getString('alf_destination');
	var documentNode = search.findNode(documentNodeStr);
	var routeAssocName = 'lecmWorkflowRoutes_selectRouteAssocFake';
	var routeNode, iterationNode, iterationNodeStr = "";


	if (json.has(routeAssocName)) {
		routeNode = search.findNode(json.getString(routeAssocName));
		iterationNode = routesService.convertRouteToIteration(documentNode, routeNode);
		if (iterationNode) {
			iterationNodeStr = iterationNode.nodeRef.toString();
		}
	} else {
		logger.log('Error processing request');
	}

	model.iterationNode = iterationNodeStr;
})();
