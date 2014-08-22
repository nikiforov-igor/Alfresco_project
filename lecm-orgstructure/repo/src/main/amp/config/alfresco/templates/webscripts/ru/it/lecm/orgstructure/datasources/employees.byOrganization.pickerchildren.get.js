<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	var data = [];

    // construct the NodeRef from the URL
    var nodeRef = url.templateArgs.organization_store + "://" + url.templateArgs.organization_store_id + "/" + url.templateArgs.organization_id;
    var availableEmployees = orgstructure.getOrganizationEmployees(nodeRef)
    if (availableEmployees != null) {
		var filter = getFilterForAvailableElement(availableEmployees);
		data = getPickerChildrenItems(filter, true);
    } else {
		data = getPickerChildrenItems(null, true);
    }

	model.parent = data.parent;
	model.rootNode = data.rootNode;
	model.results = data.results;
	model.additionalProperties = data.additionalProperties;
}

main();
