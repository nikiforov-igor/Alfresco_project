var dictionary;
var nodeRef = args["nodeRef"];
if (nodeRef == null || nodeRef == "") {
    dictionary = companyhome.childByNamePath("Dictionary");
} else {
    dictionary = search.findNode(nodeRef);
}

var dictionary_values = dictionary.getChildren();

var branch = [];

addItems(branch, dictionary_values);

model.branch = branch;

function addItems(branch, items) {
	for each(var item in items) {
		title = item.getName();
		type = getNodeType(item);
		nodeRef = item.getNodeRef().toString();
		isLeaf = true;
		if (type == "dictionary") {
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