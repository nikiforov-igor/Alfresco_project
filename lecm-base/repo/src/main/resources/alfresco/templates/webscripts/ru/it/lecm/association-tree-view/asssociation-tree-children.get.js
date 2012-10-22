var nodeRef = url.templateArgs.store_type + "://" + url.templateArgs.store_id + "/" + url.templateArgs.id;
var nodeTitleProperty = args['nodeTitleProperty'],

parentNode = search.findNode(nodeRef);
var branch = [];

if (parentNode != null) {
    var values = parentNode.getChildren();

    for each(var item in values) {
        branch.push({
            title: item.properties[nodeTitleProperty],
            type: item.getTypeShort(),
            nodeRef: item.getNodeRef().toString(),
            isLeaf: "" + !item.hasChildren
        });
    }
}

model.branch = branch;