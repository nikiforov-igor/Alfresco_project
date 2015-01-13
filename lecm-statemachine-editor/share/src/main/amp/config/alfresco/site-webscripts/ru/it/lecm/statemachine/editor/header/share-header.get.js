(function() {
	var json,
		url = '/lecm/statemachine/editor/title?' + page.url.queryString,
		headerTitle = widgetUtils.findObject(model.jsonModel, 'id', 'HEADER_TITLE');
		if (headerTitle && page.url.args && 'statemachine' == page.id) {
			json = remote.connect('alfresco').get(encodeURI(url));
			if (200 == json.status) {
				headerTitle.config.label = json + ' – ' + page.title;
			}
		}
})();
