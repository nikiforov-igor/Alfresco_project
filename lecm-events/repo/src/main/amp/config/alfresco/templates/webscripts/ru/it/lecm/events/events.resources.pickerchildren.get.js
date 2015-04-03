<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	var data = [];

	var availableResources = events.getAvailableUserResources();
	if (availableResources != null) {
		var filter = getFilterForAvailableElement(availableResources);
		data = getPickerChildrenItems(filter);
	} else {
		data = getPickerChildrenItems();
	}

	model.parent = data.parent;
	model.rootNode = data.rootNode;
	model.results = data.results;
	model.additionalProperties = data.additionalProperties;
}

main();