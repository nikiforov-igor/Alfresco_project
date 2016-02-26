var node = search.findNode(json.get("nodeRef"));
var parentStaticNode = json.has("parentStaticNodeRef") ? search.findNode(json.get("parentStaticNodeRef")) : null;

if (parentStaticNode && node) {
	model.saved = arm.saveUserColumnsSet(node, parentStaticNode, json.has("columns") ? json.get("columns").toString() : "{selected:[]}");   
}
else if (node) {
	model.saved = arm.saveUserColumnsSet(node, json.has("columns") ? json.get("columns").toString() : "{selected:[]}");
}
else {
    status.code = 404;
    status.message = "NodeRef " + args["nodeRef"] + " not found. It maybe deleted or never existed";
    status.redirect = true;
}
