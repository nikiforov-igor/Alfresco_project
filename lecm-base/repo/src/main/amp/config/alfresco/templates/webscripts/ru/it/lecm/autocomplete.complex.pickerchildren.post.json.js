<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

(function () {
	var obj = eval('(' + json.toString() + ')'),
		data = {
			parent: null,
			rootNode: null,
			results: [],
			additionalProperties: null
		};
	if (obj.elementsParams) {
		obj.elementsParams.forEach(function(elementParams) {
			data.results = data.results.concat(getPickerChildrenItems('ISNOTNULL:"sys:node-dbid"', null, true, elementParams).results);
		});
	} else {
		data = getPickerChildrenItems('ISNOTNULL:"sys:node-dbid"');
	}

	model.parent = data.parent;
	model.rootNode = data.rootNode;
	model.results = data.results;
	model.additionalProperties = data.additionalProperties;
})();