<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	var data = getPickerChildrenItems();

	var filteredResults = [];
	if (url.templateArgs.role != null) {
		var availableEmployees = orgstructure.getEmployeesByBusinessRoleId(url.templateArgs.role, true);
		if (availableEmployees != null) {
			for (var i = 0; i < data.results.length; i++) {
				for (var j = 0; j < availableEmployees.length; j++) {
					if (availableEmployees[j].nodeRef.equals(data.results[i].item.nodeRef)) {
						filteredResults.push(data.results[i]);
					}
				}
			}
		} else {
			filteredResults = data.results;
		}
	}  else {
		filteredResults = [];
	}


	model.parent = data.parent;
	model.rootNode = data.rootNode;
	model.results = filteredResults;
	model.additionalProperties = data.additionalProperties;
}

main();