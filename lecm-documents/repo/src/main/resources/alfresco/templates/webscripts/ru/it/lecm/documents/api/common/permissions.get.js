var documentRef = args['nodeRef'];
var document = search.findNode(documentRef);
model.hasReadAccess = document.hasPermission("Read");
model.hasWriteAccess = document.hasPermission("Write");