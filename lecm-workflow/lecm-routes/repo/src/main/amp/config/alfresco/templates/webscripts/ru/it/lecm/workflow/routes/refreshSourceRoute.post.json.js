(function () {
	var sourceRouteNode, sourceRouteTitle;
	var request = jsonUtils.toObject(json);
	var iterationNode = search.findNode(request.nodeRef);

	sourceRouteNode = routesService.getSourceRouteForIteration(iterationNode);

	if (sourceRouteNode) {
		sourceRouteTitle = sourceRouteNode.properties['cm:title'];
	} else {
		sourceRouteTitle = msg.get('lecm.routers.individual.route');
	}

	iterationNode.properties['cm:title'] = sourceRouteTitle;
	iterationNode.save();

})();
