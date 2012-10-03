var rootNode;
var nodeRef = args["nodeRef"];
if (nodeRef == null || nodeRef == "") {
    rootNode = companyhome.childByNamePath("Dictionary");
} else {
    rootNode = search.findNode(nodeRef);
}
var branch = [];


if (rootNode != null) {
    var dictionary_values = rootNode.getChildren();

    addItems(branch, dictionary_values);
}

model.branch = branch;

function addItems(branch, items) {
	for each(var item in items) {
		title = item.getName();
		type = getNodeType(item);
		nodeRef = item.getNodeRef().toString();
		isLeaf = true;
		if (type == "dictionary" || type == "dictionary_values") {
			isLeaf = !item.hasChildren;
		}
		branch.push({
			title: title,
			type: type,
			nodeRef: nodeRef,
			isLeaf: "" + isLeaf
		});
	}
}

function getNodeType(node) {
    var type = node.getTypeShort();
    type = type.substr(type.lastIndexOf(":") + 1);
	return type;
}