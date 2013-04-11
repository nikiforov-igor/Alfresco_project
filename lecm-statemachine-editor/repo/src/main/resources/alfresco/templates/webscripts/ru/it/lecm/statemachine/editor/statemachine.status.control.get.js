var nodeRef = args["nodeRef"];
var type = args["type"];

if (nodeRef != null) {
    var currentStatus = search.findNode(nodeRef);
	var stateMachine = currentStatus.getParent();
	var statuses = [];

    var children = stateMachine.getChildren();
	for each (var status in children) {
        //проверка строк на равенство работает только в таком формате! проверка toString() не работает!
		if ((!status.properties["lecm-stmeditor:startStatus"]) && (("" + status.nodeRef) != ("" + currentStatus.nodeRef))) {
			statuses.push({
				nodeRef: status.nodeRef.toString(),
				name: status.properties["cm:name"]
			});
		}
	}
	model.statuses = statuses;
}