var primaryDocumentNodeRef = args['primaryDocumentNodeRef'];
var	connectedDocumentNodeRef = args['connectedDocumentNodeRef'];

model.defaultConnectionType = documentConnection.getDefaultConnectionType(primaryDocumentNodeRef, connectedDocumentNodeRef);
model.availableConnectionTypes = documentConnection.getAvailableConnectionTypes(primaryDocumentNodeRef, connectedDocumentNodeRef);
model.existConnectionTypes = documentConnection.getExistConnectionTypes(primaryDocumentNodeRef, connectedDocumentNodeRef);