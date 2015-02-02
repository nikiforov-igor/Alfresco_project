<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	var data = getPickerChildrenItems(),
		rootNodeRef = url.templateArgs.store_type + "://" + url.templateArgs.store_id + "/" + url.templateArgs.id;

	var items = data.results;

	model.results = items.filter(function(el) {
		return (el.item.parent && el.item.parent.nodeRef == rootNodeRef);
	});

	model.parent = data.parent;
	model.rootNode = data.rootNode;
	model.additionalProperties = data.additionalProperties;
}

main();