<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	var data = getPickerChildrenItems(null, true);

    // construct the NodeRef from the URL
    var nodeRef = url.templateArgs.organization_store + "://" + url.templateArgs.organization_store_id + "/" + url.templateArgs.organization_id;
	var filteredResults = [];

    var availableEmployees = orgstructure.getOrganizationEmployees(nodeRef)
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

	model.parent = data.parent;
	model.rootNode = data.rootNode;
	model.results = filteredResults;
	model.additionalProperties = data.additionalProperties;
}

main();
