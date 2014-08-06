<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	var data = getPickerChildrenItems();

    // construct the NodeRef from the URL
    var nodeRef = url.templateArgs.contractor_store + "://" + url.templateArgs.contractor_store_id + "/" + url.templateArgs.contractor_id;
	var filteredResults = [];

    var availableRepresentatives = contractorsRootObject.getRepresentatives(nodeRef)
    if (availableRepresentatives != null) {
        for (var i = 0; i < data.results.length; i++) {
            for (var j = 0; j < availableRepresentatives.size(); j++) {
                if (availableRepresentatives.get(j).nodeRef.equals(data.results[i].item.nodeRef)) {
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
