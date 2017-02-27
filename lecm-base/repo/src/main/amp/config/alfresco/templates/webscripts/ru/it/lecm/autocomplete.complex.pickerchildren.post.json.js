<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

function main() {
	var itemsParams = json.has('elementsParams') ? json.get('elementsParams') : null,
		data = {},
		dataPart,
		result;
	if (itemsParams && itemsParams.length()) {
		data.results = [];
		for (var i=0; i < itemsParams.length(); ++i) {
			var itemsParamObj = eval('(' + itemsParams.get(i) + ')');
			dataPart = getPickerChildrenItems('ISNOTNULL:"sys:node-dbid"', null, true, itemsParamObj);
			data.results = data.results.concat(dataPart.results);

			data.parent = dataPart.parent;
			data.rootNode = dataPart.rootNode;
			data.additionalProperties = dataPart.additionalProperties;
		}
	}
	else {
		data = getPickerChildrenItems('ISNOTNULL:"sys:node-dbid"');
	}

	model.parent = data.parent;
	model.rootNode = data.rootNode;
	model.results = data.results;
	model.additionalProperties = data.additionalProperties;
}

main();
