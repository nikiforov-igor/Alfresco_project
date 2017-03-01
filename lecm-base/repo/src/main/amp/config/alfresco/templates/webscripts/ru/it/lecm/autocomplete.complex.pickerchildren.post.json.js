<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

(function () {
	var obj = eval('(' + json.toString() + ')'),
		dataPart,
		data = data = {
			results: [],
			parent: null,
			rootNode: null,
			additionalProperties: null
		};
	if (obj.elementsParams && obj.elementsParams.length) {
		for (var i=0; i < obj.elementsParams.length; ++i) {
			dataPart = getPickerChildrenItems('ISNOTNULL:"sys:node-dbid"', null, true, obj.elementsParams[i]);
			data.results = data.results.concat(dataPart.results);
		}
	}
	else {
		data = getPickerChildrenItems('ISNOTNULL:"sys:node-dbid"');
	}

	model.parent = data.parent;
	model.rootNode = data.rootNode;
	model.results = data.results;
	model.additionalProperties = data.additionalProperties;
})();