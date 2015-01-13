(function() {
	var json,
		title = 'Жизненный цикл',
		url = '/lecm/statemachine/editor/title?' + page.url.queryString,
		headerTitle = widgetUtils.findObject(model.jsonModel, 'id', 'HEADER_TITLE');
		if (headerTitle) {
			if (page.url.args && 'statemachine' == page.id) {
				json = remote.connect('alfresco').get(encodeURI(url));
				if (200 == json.status) {
					title = json + ' – ' + title;
				}
				headerTitle.config.label = title;
			}
		}
})();
