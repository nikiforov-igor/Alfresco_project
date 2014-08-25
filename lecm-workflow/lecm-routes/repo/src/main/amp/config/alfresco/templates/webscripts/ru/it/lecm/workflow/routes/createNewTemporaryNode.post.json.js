(function(){
	var request = jsonUtils.toObject(json);
	var node = routesService.createNewTemporaryNode(request.destination, request.nodeType);
	model.nodeRef = node.nodeRef.toString();
})();
