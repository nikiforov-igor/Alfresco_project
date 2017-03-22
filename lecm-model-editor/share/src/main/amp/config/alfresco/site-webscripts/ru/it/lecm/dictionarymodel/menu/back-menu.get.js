(function() {
	var url, type, result, json,
		nodeRef = page.url.args['nodeRef'],
		docType = page.url.args['doctype'],
		statemachineId = page.url.args['statemachineId'];

	type = ((docType) ? docType : ((statemachineId) ? statemachineId : '')).replace('_', ':');

	url = '/lecm/docmodels/item?nodeRef=' + nodeRef + '&type=' + type;

	result = remote.connect('alfresco').get(url);
	if (200 == result.status) {
		json = eval('(' + result + ')');
		model.modelItem = {
			nodeRef: json.nodeRef,
			isDocumentModel: json.isDocumentModel,
			isModelActive: json.isModelActive,
			typeName: json.typeName
		}
	}
})();
