(function() {
	var json,
		url,
		headerTitle = widgetUtils.findObject(model.jsonModel, 'id', 'HEADER_TITLE');
		if (headerTitle && page.url.args && 'doc-forms-list' == page.id) {
			url = '/lecm/statemachine/editor/title?statemachineId=' + page.url.args['doctype'];
			json = remote.connect('alfresco').get(encodeURI(url));
			if (200 == json.status) {
				headerTitle.config.label = json + ' – ' + page.title;
			}
		}
})();
