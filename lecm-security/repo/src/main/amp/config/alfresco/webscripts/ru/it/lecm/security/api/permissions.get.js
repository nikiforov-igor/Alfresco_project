var nodeRef = args['nodeRef'];
var permissions = args['permissions'].split(",");

var results = [];
if (permissions != null) {
	for (var i = 0; i < permissions.length; i++) {
		results.push(lecmPermission.hasPermission(nodeRef, permissions[i]));
	}
}
model.permissions = results;