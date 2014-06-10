var documentRef = args['nodeRef'];
var document = search.findNode(documentRef);
if (document) {
	model.hasReadAccess = document.hasPermission("Read");
	model.hasWriteAccess = document.hasPermission("Write");
} else {
	status.code = 404;
	status.message = "NodeRef " + args["nodeRef"] + " not found. It maybe deleted or never existed";
	status.redirect = true;
}
