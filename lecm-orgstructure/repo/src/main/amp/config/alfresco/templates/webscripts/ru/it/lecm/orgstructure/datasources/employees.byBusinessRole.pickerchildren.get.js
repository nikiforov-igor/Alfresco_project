<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	var data = [];
	if (url.templateArgs.role != null) {
		var availableEmployees = orgstructure.getEmployeesByBusinessRoleId(url.templateArgs.role, true);
		var filter = getFilterForAvailableElement(availableEmployees);
		data = getPickerChildrenItems(filter);
	}

	model.parent = data.parent;
	model.rootNode = data.rootNode;
	model.results = data.results;
	model.additionalProperties = data.additionalProperties;
}

main();