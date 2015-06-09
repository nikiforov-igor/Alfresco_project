<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	var data = [],
		results = [],
		converted = [];

	data = getPickerChildrenItems();
	results = data.results;

	for each(result in results) {
		var convertedRes = {};
		var item = orgstructure.getEmployeeByPosition(result.item);
		if(item) {
			convertedRes.item = item;
			convertedRes.selectable = true;
			converted.push(convertedRes);
		}
	}


//	model.parent = data.parent;
//	model.rootNode = data.rootNode;
	model.results = converted;
//	model.additionalProperties = data.additionalProperties;
}

main();