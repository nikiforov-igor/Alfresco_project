<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	var filter = '@lecm-document-aspects\\:reg-data-is-registered:"true"';
	if (args["searchSimilar"]) {
		var document = search.findNode(args["documentRef"]);
		if (document != null) {
			filter = addSimilarFilter(filter, document, "lecm-document:subject-assoc-ref");
			filter = addSimilarFilter(filter, document, "lecm-incoming:sender-assoc-ref");
			filter = addSimilarFilter(filter, document, "lecm-incoming:addressee-assoc-ref");
		}
	}

	var data = getPickerChildrenItems(filter);

	model.parent = data.parent;
	model.rootNode = data.rootNode;
	model.results = data.results;
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