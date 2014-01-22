var documentNodeRef = args['documentNodeRef'];
var previosDocRef = args['previosDocRef'];

var items = [];

var rootFolder = documentConnection.getRootFolder(documentNodeRef);
var childConnections = rootFolder.getChildren();
var parentConnections = documentConnection.getConnectionsWithDocument(documentNodeRef, false);
if (parentConnections != null && parentConnections.length > 0) {
	for (var i = 0; i < parentConnections.length; i++) {
		var connection = parentConnections[i];
		if (connection.assocs["lecm-connect:primary-document-assoc"]) {
			if (!previosDocRef || connection.assocs["lecm-connect:primary-document-assoc"][0].nodeRef != previosDocRef) {
				items.push(connection);
			}
		}
	}
}
if (childConnections != null && childConnections.length > 0) {
	for (var i = 0; i < childConnections.length; i++) {
		var connection = childConnections[i];
		if (connection.assocs["lecm-connect:connected-document-assoc"]) {
			if (!previosDocRef || connection.assocs["lecm-connect:connected-document-assoc"][0].nodeRef != previosDocRef) {
				items.push(connection);
			}
		}
	}
}

model.items = items;
model.documentRef = documentNodeRef;
