var documentNodeRef = args['documentNodeRef'];
var count = parseInt(args['count']);


var rootFolder = documentConnection.getRootFolder(documentNodeRef);

var items = [];
var hasNext = false;
var k = 0;

model.documentService = documentScript;
if (null != rootFolder){
	var connections = rootFolder.getChildren();
	if (connections != null && connections.length > 0) {
		for (var i = 0; i < connections.length; i++) {
			if (k < count) {
				var connectedDocumentAssoc = connections[i].assocs["lecm-connect:connected-document-assoc"];
				if (connectedDocumentAssoc != null && connectedDocumentAssoc.length == 1 && connectedDocumentAssoc[0].exists()) {
					items.push(connections[i]);
					k++;
				}
			} else {
				hasNext = true;
			}
		}
	}
}

model.items = items;
model.hasNext = hasNext;