(function() {
	var documentNodeStr = json.getString('documentNodeRef');
	var documentNode = search.findNode(documentNodeStr);
	var iterationNode, iterationNodeStr;


	iterationNode = routesService.createIterationFromPrevious(documentNode);
	if (iterationNode) {
		iterationNodeStr = iterationNode.nodeRef.toString();
	}

	model.iterationNode = iterationNodeStr ? iterationNodeStr : '';
})();
