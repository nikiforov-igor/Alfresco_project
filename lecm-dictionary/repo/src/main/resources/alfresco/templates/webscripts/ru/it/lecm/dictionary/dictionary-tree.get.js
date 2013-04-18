/**
 * Получение списка дочерних значений выбранного узла и их свойств
 */
var rootNode;

var assocType = "cm:contains";
var argsAssocType = args["assocType"];
if (argsAssocType != null && argsAssocType != "") {
    assocType = argsAssocType;
}

var nodeRef = args["nodeRef"];
if (nodeRef == null || nodeRef == "") {
    rootNode = companyhome.childByNamePath("Dictionary");
} else {
    rootNode = search.findNode(nodeRef);
}
var branch = [];


if (rootNode != null) {
    var dictionary_values = rootNode.childAssocs[assocType];

    addItems(branch, dictionary_values);
}

model.branch = branch;

function addItems(branch, items) {
	for each(var item in items) {
		if (item.properties["lecm-dic:active"]) {
			title = item.getName();
			type = getNodeType(item);
			nodeRef = item.getNodeRef().toString();

			var childs = item.childAssocs[assocType];
			var activeChildsCount = 0;
			if (childs != null) {
				for (var j = 0; j < childs.length; j++) {
					if (childs[j].properties["lecm-dic:active"]) {
						activeChildsCount++;
					}
				}
			}

			isLeaf = activeChildsCount == 0;
			branch.push({
				title: title,
				type: type,
				nodeRef: nodeRef,
				isLeaf: "" + isLeaf
			});
		}
	}
}

function getNodeType(node) {
    var type = node.getTypeShort();
    type = type.substr(type.lastIndexOf(":") + 1);
	return type;
}