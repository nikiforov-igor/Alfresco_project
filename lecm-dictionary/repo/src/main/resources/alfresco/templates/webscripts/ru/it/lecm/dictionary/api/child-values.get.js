<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/dictionary/api/dictionary.lib.js">

var nodeRef = args["nodeRef"];
if (nodeRef != null && nodeRef.length > 0) {
	var assocType = "cm:contains";
	var argsAssocType = args["assocType"];
	if (argsAssocType != null && argsAssocType != "") {
		assocType = argsAssocType;
	}
	var dicValue = search.findNode(nodeRef);
	if (dicValue != null) {
		var childs = dicValue.childAssocs[assocType];
		if (childs != null && childs.length > 0) {
			var items = [];
			for (var i = 0; i < childs.length; i++) {
				if (childs[i].properties["lecm-dic:active"]) {
					var item = {
						node: childs[i],
						propertiesName: getTypePropertiesName(childs[i])
					};
					items.push(item);
				}
			}
			model.items = items;
		}
	}
}