<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	var data = [];

	var fromDate = args['fromDate'];
	var toDate = args['toDate'];
	var event = args['eventNodeRef'];

	var availableLocations = events.getAvailableUserLocations(fromDate, toDate, event);
	if (availableLocations != null) {
		var filter = getFilterForAvailableElement(availableLocations);
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