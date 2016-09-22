<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">
function main() {
	var data = [],
		hasGlobalAccess = orgstructure.hasGlobalOrganizationsAccess();

	if (hasGlobalAccess) {/*организация сотрудника*/
		data = getPickerChildrenItems();
	} else {
		var org = orgstructure.getEmployeeOrganization(orgstructure.getCurrentEmployee());

		var filterAr = [];
		filterAr.push(org);

		var filter = getFilterForAvailableElement(filterAr);
		data = getPickerChildrenItems(filter);
	}

	model.parent = data.parent;
	model.rootNode = data.rootNode;
	model.results = data.results;
	model.additionalProperties = data.additionalProperties;
};

main();