var node = search.findNode(json.get("nodeRef"));
if (node) {
	model.saved = arm.saveUserColumnsSet(node, json.has("columns") ? json.get("columns").toString() : "{selected:[]}");
}
else {
    status.code = 404;
    status.message = "NodeRef " + args["nodeRef"] + " not found. It maybe deleted or never existed";
    status.redirect = true;
}