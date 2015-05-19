var documentNodeRef = args['documentNodeRef'];


model.documentService = documentScript;
model.items = documentConnection.getConnectionsWithDocument(documentNodeRef);