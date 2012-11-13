<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/dictionary/api/dictionary.lib.js">

var nodeRef = args["nodeRef"];
var version = args["version"];

if (nodeRef != null && nodeRef.length > 0) {
	var dicValue = search.findNode(nodeRef);

	if (dicValue != null && dicValue.properties["lecm-dic:active"]) {
		if (version != null && version.length > 0) {
			var versionNode = dicValue.getVersion(version);
			if (versionNode != null) {
				dicValue = versionNode.getNode();
			} else {
				dicValue = null;
			}
		}

		if (dicValue != null) {
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
}