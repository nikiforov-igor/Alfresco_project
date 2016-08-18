/* global companyhome, search, model, args, substitude */

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
		if (item.isSubType("lecm-dic:hierarchical_dictionary_values") && item.properties["lecm-dic:active"]) {
			nodeRef = item.getNodeRef().toString();

			var childs = item.childAssocs[assocType];
            var isLeaf = true;
			if (childs != null) {
				for (var j = 0; j < childs.length; j++) {
					if (childs[j].isSubType("lecm-dic:hierarchical_dictionary_values") && childs[j].properties["lecm-dic:active"]) {
						isLeaf = false;
                        break;
					}
				}
			}

			var canPush = 'lecm-contractor:contractor-type' != item.typeShort || !item.hasAspect('lecm-orgstr-aspects:is-organization-aspect');

			if (canPush) {
				branch.push({
					title: substitude.getObjectDescription(item),
					type: item.typeShort,
					childType: getHierarchicalDictionaryChildType(item),
					nodeRef: nodeRef,
					isLeaf: "" + isLeaf,
					node: item
				});
			}
		}
	}
	branch.sort(sortBranch);
}

function sortBranch(item1, item2) {
	var val1 = item1.title.toUpperCase(),
		val2 = item2.title.toUpperCase();
	return (val1 > val2) ? 1 : (val1 < val2) ? -1 : 0;
}

function getHierarchicalDictionaryChildType(node) {
	var type = node.properties["lecm-dic:valueContainsType"];

	if (type == null || type.length == 0) {
		while(node.typeShort != "lecm-dic:dictionary"){
			node = node.parent;
		}
		if (node.properties["lecm-dic:type"]!=null){
			type = node.properties["lecm-dic:type"];
		}
	}
	return type;
}
