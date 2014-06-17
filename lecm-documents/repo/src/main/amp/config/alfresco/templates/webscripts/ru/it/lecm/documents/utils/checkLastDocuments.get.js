var node = search.findNode(args["nodeRef"]);
if (node) {
	model.saved = documentScript.checkLastDocuments(node);
} else {
	status.code = 404;
	status.message = "NodeRef " + args["nodeRef"] + " not found. It maybe deleted or never existed";
	status.redirect = true;
}
