<import resource="classpath:/alfresco/templates/ru/it/lecm/model-editor/isAdmin.js">

(function() {
	var doctype = url.args.doctype,
		getUri = '/lecm/controls-editor/getTypeRoot',
		createUri = '/lecm/controls-editor/createTypeRoot',
		params = '?doctype=' + encodeURIComponent(doctype),
		connector = remote.connect('alfresco'),
		typeRoot = null,
		json, obj;

	json = connector.get(getUri + params);
	if (200 == json.status) {
		obj = eval('(' + json + ')');
		typeRoot = obj.typeRoot;
		if (!typeRoot) {
			json = connector.post(createUri + params, '{}', 'application/json');
			if (200 == json.status) {
				obj = eval('(' + json + ')');
				typeRoot = obj.typeRoot;
			}
		}
	}
	context.page.properties['typeRoot'] = typeRoot;
})();
