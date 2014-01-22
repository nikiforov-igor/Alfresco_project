<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	var data = getPickerChildrenItems();

	var filteredResults = [];
	for (var i = 0; i < data.results.length; i++) {
		if (documentScript.getDocumentRegData(data.results[i].item) != null) {
			filteredResults.push(data.results[i]);
		}
	}

	model.parent = data.parent;
	model.rootNode = data.rootNode;
	model.results = filteredResults;
	model.additionalProperties = data.additionalProperties;
}

main();