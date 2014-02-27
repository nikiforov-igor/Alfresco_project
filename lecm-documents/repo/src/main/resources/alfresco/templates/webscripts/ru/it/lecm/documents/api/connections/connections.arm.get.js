function main() {
    var nodeRef = args["nodeRef"];
	model.connections = documentConnection.getConnections(nodeRef);
	model.connectionsWithDocument = documentConnection.getConnectionsWithDocument(nodeRef);
}

main();