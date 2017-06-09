var nodeRef = args["nodeRef"];

var node = search.findNode(nodeRef);
if (node) {
    var folderAssocs = node.assocs["lecm-document-aspects:complex-attachment-folder"];
    if (folderAssocs && folderAssocs.length) {
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

        var jsonNodes = nodes.map(function (item) {
            return item.toJSON(true);
        });

        model.nodes = jsonNodes;
        model.totalPageNum = folder.getChildren().length;
        model.folderNodeRef = folder.nodeRef.toString();
    }
}

