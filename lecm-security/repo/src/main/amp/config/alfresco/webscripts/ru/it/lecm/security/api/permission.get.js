var nodeRef = args['nodeRef'];
var permissionStr = args['permission'];

model.permission = lecmPermission.hasPermission(nodeRef, permissionStr);