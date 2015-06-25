(function() {
	var units = args['items'].split(',');
	for each(unit in units) {
		var unitNodeRef = search.findNode(unit);
		var unitChildren = unitNodeRef.getChildren();
		if(!!unitChildren.length) {
			model.result = true;
			return;
		}
	}

	model.result = false;
})();