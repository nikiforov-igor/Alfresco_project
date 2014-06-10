var nodeRef = args['nodeRef'];
var permissionStr = args['permission'];
var hasPerm = lecmPermission.hasPermission(nodeRef, permissionStr);
model.permission = statemachine.isDraft(search.findNode(nodeRef)) && hasPerm;
