(function() {

	var rootNodeStr = args['nodeRef'];
	var rootNode = search.findNode(rootNodeStr);

	var query = 'PATH:"' + rootNode.qnamePath + '//*" AND TYPE:"lecm-os:nomenclature-case" AND @lecm-os\\:nomenclature-case-status:"CLOSED" AND @lecm-os\\:nomenclature-case-to-archive:true';

	model.forArchive = search.luceneSearch(query) || [];

	query = 'PATH:"' + rootNode.qnamePath + '//*" AND TYPE:"lecm-os:nomenclature-case" AND @lecm-os\\:nomenclature-case-status:"MARK_TO_DESTROY"';

	model.forDestroy = search.luceneSearch(query) || [];

})();