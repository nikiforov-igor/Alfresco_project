var documentNodeRef = args['documentNodeRef'];
var count = parseInt(args['count']);


var rootFolder = documentConnection.getRootFolder(documentNodeRef);

var items = [];
var hasNext = false;
var k = 0;

var connections = rootFolder.getChildren();
if (connections != null && connections.length > 0) {
	for (var i = 0; i < connections.length; i++) {
		if (k < count) {
			items.push(connections[i]);
		} else {
			hasNext = true;
		}
		k++;
	}
}

model.items = items;
model.hasNext = hasNext;