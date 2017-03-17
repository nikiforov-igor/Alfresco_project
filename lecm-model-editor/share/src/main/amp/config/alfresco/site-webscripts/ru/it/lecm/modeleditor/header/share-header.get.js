(function() {
	var json,
		url,
		headerTitle = widgetUtils.findObject(model.jsonModel, 'id', 'HEADER_TITLE');
		if (headerTitle && page.url.args && 
				('doc-model-edit' == page.id||'doc-forms-list' == page.id||'doc-controls-list' == page.id||'doc-model-view' == page.id||
				 'dict-model-edit' == page.id||'dict-controls-list' == page.id||'dict-forms-list' == page.id||'dict-model-view' == page.id)
		) {
			url = '/lecm/statemachine/editor/title?statemachineId=' + page.url.args['doctype'];
			json = remote.connect('alfresco').get(encodeURI(url));
			if (200 == json.status) {
				headerTitle.config.label = json + ' – ' + page.title;
			}
		}
})();
