<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	var data = getPickerChildrenItems();

	var filteredResults = [];
	var availableExecutors = errands.getAvailableExecutors();
	if (availableExecutors != null) {
		for (var i = 0; i < data.results.length; i++) {
			if (availableExecutors.contains(data.results[i].item.nodeRef)) {
				filteredResults.push(data.results[i]);
			}
		}
	} else {
		filteredResults = data.results;
	}

	model.parent = data.parent;
	model.rootNode = data.rootNode;
	model.results = filteredResults;
	model.additionalProperties = data.additionalProperties;
}

main();