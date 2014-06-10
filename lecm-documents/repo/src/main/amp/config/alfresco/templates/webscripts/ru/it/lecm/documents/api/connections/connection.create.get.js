var primaryDocumentNodeRef = args['primaryDocumentNodeRef'];
var connectedDocumentNodeRef = args['connectedDocumentNodeRef'];
var typeNodeRef = args['typeNodeRef'];

model.connection = documentConnection.createConnection(primaryDocumentNodeRef, connectedDocumentNodeRef, typeNodeRef);