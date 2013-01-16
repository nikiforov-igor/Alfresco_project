var nodeRef = args["nodeRef"];
var type = args["type"];

if (nodeRef != null) {

	var node;
	if (type == "create") {
		node = search.findNode(nodeRef);
	} else {
		node = search.findNode(nodeRef).getParent();
	}
	var roles = node.getParent().getParent().getParent().getParent().childByNamePath("roles");


	var children = roles.getChildren();
	var result = [];
	for each (var role in children) {
		result.push({
			nodeRef: role.assocs["lecm-stmeditor:role-assoc"][0].nodeRef.toString(),
			name: role.assocs["lecm-stmeditor:role-assoc"][0].properties["cm:name"]
		});

	}
	model.roles = result;
}