function main() {
    var nodeRef = args["itemId"];
	var connections = [];
	var connectionsWithDocument = [];
	if (lecmPermission.hasPermission(nodeRef, "_lecmPerm_LinksView")) {
		var tempConnections = documentConnection.getConnections(nodeRef);
		for (var i = 0; i < tempConnections.length; i++) {
			var document = tempConnections[i].assocs["lecm-connect:connected-document-assoc"][0];
			if (document != null && lecmPermission.hasReadAccess(document)) {
				connections.push(tempConnections[i]);
			}
		}
		var tempConnectionsWithDocument = documentConnection.getConnectionsWithDocument(nodeRef);
		for (i = 0; i < tempConnectionsWithDocument.length; i++) {
			document = tempConnectionsWithDocument[i].assocs["lecm-connect:primary-document-assoc"][0];
			if (document != null && lecmPermission.hasReadAccess(document)) {
				connectionsWithDocument.push(tempConnectionsWithDocument[i]);
			}
		}
	}
	model.connections = connections;
	model.connectionsWithDocument = connectionsWithDocument;
	model.documentService = documentScript;
}

main();