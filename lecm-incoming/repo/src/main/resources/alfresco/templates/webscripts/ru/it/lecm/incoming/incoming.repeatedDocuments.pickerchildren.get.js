<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	var filter = '';
	if (args["searchSimilar"]) {
		var document = search.findNode(args["documentRef"]);
		if (document != null) {
			filter = addSimilarFilter(filter, document, "lecm-incoming:subject-assoc-ref");
		}
	}

	var data = getPickerChildrenItems(filter);

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

function addSimilarFilter(filter, document, prop) {
	var propValue = document.properties[prop];
	if (propValue != null) {
		if (filter.length > 0) {
			filter += " AND ";
		}
		filter += '@' + prop.replace(":", "\\:") + ':"' + propValue + '"';
	}
	return filter;
}

main();