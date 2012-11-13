var nodeRef = url.templateArgs.store_type + "://" + url.templateArgs.store_id + "/" + url.templateArgs.id;
var nodeTitleProperty = args['nodeTitleProperty'],

parentNode = search.findNode(nodeRef);
var branch = [];

if (parentNode != null) {
    var values = parentNode.getChildren();

    for each(var item in values) {
		if (!item.hasAspect("lecm-dic:aspect_active") || item.properties["lecm-dic:active"]) {
	        branch.push({
	            title: item.properties[nodeTitleProperty],
	            type: item.getTypeShort(),
	            nodeRef: item.getNodeRef().toString(),
	            isLeaf: "" + !item.hasChildren,
	            isContainer: "" + item.isContainer
	        });
		}
    }
}

model.branch = branch;