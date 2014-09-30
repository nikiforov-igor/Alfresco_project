(function () {
	var sourceRouteNode, sourceRouteTitle;
	var request = jsonUtils.toObject(json);
	var iterationNode = search.findNode(request.nodeRef);

	sourceRouteNode = routesService.getSourceRouteForIteration(iterationNode);

	if (sourceRouteNode) {
		sourceRouteTitle = sourceRouteNode.properties['cm:title'];
	} else {
		sourceRouteTitle = "Индивидуальный маршрут";
	}

	iterationNode.properties['cm:title'] = sourceRouteTitle;
	iterationNode.save();

})();
