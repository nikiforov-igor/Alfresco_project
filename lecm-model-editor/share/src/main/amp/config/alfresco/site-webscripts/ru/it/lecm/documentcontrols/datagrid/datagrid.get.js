(function() {
	var doctype = args['doctype'],
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
	model.doctype = doctype;
	model.bubblingLabel = args['bubblingLabel'];
	model.typeRoot = typeRoot;
})();
