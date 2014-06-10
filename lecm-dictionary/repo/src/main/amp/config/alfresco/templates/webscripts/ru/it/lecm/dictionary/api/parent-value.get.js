<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/dictionary/api/dictionary.lib.js">

var nodeRef = args["nodeRef"];
if (nodeRef != null && nodeRef.length > 0) {
	var dicValue = search.findNode(nodeRef);
	if (dicValue != null) {
		var parent = dicValue.parent;
		if (parent != null) {
			var item = {
				node: parent,
				propertiesName: getTypePropertiesName(parent)
			};
			model.item = item;
		}
	}
}