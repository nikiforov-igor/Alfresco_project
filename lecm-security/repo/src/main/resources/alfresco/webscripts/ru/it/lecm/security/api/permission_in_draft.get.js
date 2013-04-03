var nodeRef = args['nodeRef'];
var permissionStr = args['permission'];
var hasPerm = lecmPermission.hasPermission(nodeRef, permissionStr);
model.permission = person.properties["cm:userName"] == "admin" || (statemachine.isDraft(search.findNode(nodeRef)) && hasPerm) || !hasPerm;