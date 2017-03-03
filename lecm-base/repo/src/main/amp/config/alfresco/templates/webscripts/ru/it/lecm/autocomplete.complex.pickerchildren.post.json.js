<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/pickerchildren.lib.js">

(function () {
	var obj = eval('(' + json.toString() + ')'),
		results = [];
	if (obj.elementsParams && obj.elementsParams.length) {
		obj.elementsParams.forEach(function(elementParams) {
			results = results.concat(getPickerChildrenItems('ISNOTNULL:"sys:node-dbid"', null, true, elementParams).results);
		});
	} else {
		status.code = 422;
		status.message = 'Insufficient parameter "elementsParams". It must be not empty array.';
		status.redirect = true;
	}
	model.results = results;
})();