<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	var data = [];

	var filteredResults = [],
		initiatorId = url.templateArgs.initiator_id,
		availableExecutors,
		initiatorNodeRef;

	if(initiatorId) {
		initiatorNodeRef = 'workspace://SpacesStore/' + initiatorId;
		availableExecutors = errands.getAvailableExecutors(initiatorNodeRef);
	} else {
		availableExecutors = errands.getAvailableExecutors();
	}
	if (availableExecutors != null) {
		var filter = getFilterForAvailableElement(availableExecutors);
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