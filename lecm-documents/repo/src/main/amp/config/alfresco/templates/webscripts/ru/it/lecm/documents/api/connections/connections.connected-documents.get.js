var nodeRef = args['nodeRef'];

var rootFolder = documentConnection.getRootFolder(nodeRef);

var items = [];
var connections = rootFolder.getChildren();

if (connections != null && connections.length > 0) {
	for (var i = 0; i < connections.length; i++) {
		if (connections[i].assocs["lecm-connect:connected-document-assoc"] != null &&
			connections[i].assocs["lecm-connect:connected-document-assoc"][0] != null) {

			items.push(connections[i].assocs["lecm-connect:connected-document-assoc"][0]);
		}
	}
}

model.items = items;