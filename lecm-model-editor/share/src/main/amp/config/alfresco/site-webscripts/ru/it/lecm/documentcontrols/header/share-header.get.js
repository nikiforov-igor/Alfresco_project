(function() {
	var json,
		url,
		title = 'Контролы',
		headerTitle = widgetUtils.findObject(model.jsonModel, 'id', 'HEADER_TITLE');
		if (headerTitle) {
			if (page.url.args && 'doc-controls-list' == page.id) {
				url = '/lecm/statemachine/editor/title?statemachineId=' + page.url.args['doctype'];
				json = remote.connect('alfresco').get(encodeURI(url));
				if (200 == json.status) {
					title = json + ' – ' + title;
				}
				headerTitle.config.label = title;
			}
		}
})();
