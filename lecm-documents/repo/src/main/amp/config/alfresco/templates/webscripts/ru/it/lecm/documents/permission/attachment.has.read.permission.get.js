var nodeRef = args["nodeRef"];
var aspect = args["aspect"];
var permission = args["permission"];
var user = args["user"];

var accept = true;

if (nodeRef && aspect && permission){
	attach = search.findNode(nodeRef);
	if (attach){
		var hasAspect = attach.hasAspect(aspect);
		if (hasAspect){
			var readPermission = lecmPermission.hasPermission(nodeRef, permission, user);
			accept = hasAspect && readPermission;
		}
	}
}

model.result = accept;