var nodeRef = args["nodeRef"];

var node = search.findNode(nodeRef);

var folder = node.assocs["lecm-document-aspects:complex-attachment-folder"][0];

var nodes = folder.getChildren();

nodes.sort(function (a, b) {
    if (a.properties['cm:name'] > b.properties['cm:name']) {
        return 1;
    }
    if (a.properties['cm:name'] < b.properties['cm:name']) {
        return -1;
    }

    return 0;
});

var jsonNodes = [];
for each (var workFileNode in nodes) {
    jsonNodes.push(workFileNode.toJSON(true));
}

model.nodes = jsonNodes;

model.totalPageNum = folder.getChildren().length;

model.folderNodeRef = folder.nodeRef.toString();

