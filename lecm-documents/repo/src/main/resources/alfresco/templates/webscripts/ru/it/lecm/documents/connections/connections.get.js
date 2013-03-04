var primaryDocumentNodeRef = args['primaryDocumentNodeRef'];
var skipItemsCount = parseInt(args["skipItemsCount"]);
var loadItemsCount = parseInt(args["loadItemsCount"]);

model.connections = documentConnection.getConnections(primaryDocumentNodeRef, skipItemsCount, loadItemsCount);
model.next = documentConnection.getConnections(primaryDocumentNodeRef, skipItemsCount + loadItemsCount, 1);