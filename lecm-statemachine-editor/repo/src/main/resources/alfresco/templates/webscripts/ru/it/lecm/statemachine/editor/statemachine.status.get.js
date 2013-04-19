var nodeRef = args["nodeRef"];
var type = args["type"];

if (nodeRef != null) {
	var action;
	if (type == "create") {
		action = search.findNode(nodeRef);
	} else {
		var transition = search.findNode(nodeRef);
		action = transition.getParent();
	}
	var status = action.getParent().getParent();
	var stateMachine = status.getParent();
	var statuses = [];
	var children = stateMachine.getChildren();
	for each (var status in children) {
		if (!status.properties["lecm-stmeditor:forDraft"]) {
			statuses.push({
				nodeRef: status.nodeRef.toString(),
				name: status.properties["cm:name"]
			});
		}

	}
	model.statuses = statuses;
}