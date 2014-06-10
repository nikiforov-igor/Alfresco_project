var nodeRef = args["nodeRef"];

if (nodeRef != null) {
	var stateMachine = search.findNode(nodeRef);
	var statuses = [];
	var children = stateMachine.childByNamePath("statuses").getChildren();
	for each (var status in children) {
		if (status.getTypeShort() != "lecm-stmeditor:endEvent") {
			statuses.push({
				nodeRef: status.nodeRef.toString(),
				name: status.properties["cm:name"]
			});
		}

	}
	model.statuses = statuses;
}