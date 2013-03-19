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

    var initRoles = node.getChildren();
    var initRolesAssocs = [];
    for each (var role in initRoles) {
        initRolesAssocs[role.assocs["lecm-stmeditor:role-assoc"][0].nodeRef.toString()] = 1;
    }


	var children = roles.getChildren();
	var result = [];
	for each (var role in children) {
        var roleNodeRef = role.assocs["lecm-stmeditor:role-assoc"][0].nodeRef.toString();
        if (initRolesAssocs[roleNodeRef] == null) {
            result.push({
                nodeRef: roleNodeRef,
                name: role.assocs["lecm-stmeditor:role-assoc"][0].properties["cm:name"]
            });
        }
	}
	model.roles = result;
}