<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	var data = [];

    // construct the NodeRef from the URL
    var nodeRef = url.templateArgs.contractor_store + "://" + url.templateArgs.contractor_store_id + "/" + url.templateArgs.contractor_id;
    var availableRepresentatives = contractorsRootObject.getRepresentatives(nodeRef)
    if (availableRepresentatives != null) {
		var filter = "ID:\"NOT_REF\"";
		for (var i = 0; i < availableRepresentatives.size(); i++) {
			filter += " OR ID:\"" + availableRepresentatives.get(i).nodeRef + "\"";
		}
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
