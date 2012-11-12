<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/dictionary/api/dictionary.lib.js">

var nodeRef = args["nodeRef"];

if (nodeRef != null && nodeRef.length > 0) {
	var dicValue = search.findNode(nodeRef);

	if (dicValue != null && dicValue.properties["lecm-dic:active"]) {
		var propertiesToSkip = {
			"lecm-dic:active": true
		};

		var item = {
			node: dicValue,
			propertiesName: getPropertiesName(dicValue, propertiesToSkip)
		};
		model.item = item;
	}
}