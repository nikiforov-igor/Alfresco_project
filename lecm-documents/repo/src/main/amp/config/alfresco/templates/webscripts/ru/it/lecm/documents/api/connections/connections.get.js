var documentNodeRef = args['documentNodeRef'];
var count = parseInt(args['count']);
var exclude = args['exclErrands'] ? ("" + args['exclErrands']) == "true" : false;


var rootFolder = documentConnection.getRootFolder(documentNodeRef);

var items = [];
var hasNext = false;
var k = 0;

model.documentService = documentScript;
model.lecmPermission = lecmPermission;

if (null != rootFolder){
	var document = search.findNode(documentNodeRef);
	var excludeErrands = document != null && document.isSubType("lecm-eds-document:base") && exclude;

	var connections = rootFolder.getChildren();
	if (connections != null && connections.length > 0) {
		for (var i = 0; i < connections.length; i++) {
			if (k < count) {
				var connectedDocumentAssoc = connections[i].assocs["lecm-connect:connected-document-assoc"];
				if (connectedDocumentAssoc != null && connectedDocumentAssoc.length == 1
					&& connectedDocumentAssoc[0].exists()
					&& (!connectedDocumentAssoc[0].isSubType("lecm-errands:document") || !excludeErrands || !connections[i].properties['lecm-connect:is-system'])) {
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