(function() {
	var fileName = args['fileName'];
	//найти nodeRef по fileName желательно без использования solr. вытащить nodeRef, nodeType и fileName
	var modelNode;
	var results = search.query({
		query: '/app:company_home/app:dictionary/app:models/cm:' + fileName,
		language: 'xpath'
	});
	if (results && results.length) {
		modelNode = results[0];
		model.nodeRef = modelNode.nodeRef.toString();
		model.nodeType = modelNode.isContainer ? 'folder' : 'document';
		model.fileName = modelNode.fileName;
	}
})();
