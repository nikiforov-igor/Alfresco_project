(function() {
	var unitStr = args['nodeRef'];
	var unitNodeRef = search.findNode(unitStr);
	var unitChildren = unitNodeRef.getChildren();
	model.result = !!unitChildren.length;
})()