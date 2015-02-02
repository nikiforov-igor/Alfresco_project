(function() {
	var nodeRef = search.findNode(args['nodeRef']); //годовой раздел номенклатуры дел
	var path = nodeRef.qnamePath;
	var result = search.query({
		language: 'fts-alfresco',
		query: 'PATH:"' + path + '//*" AND +TYPE:"lecm-os:nomenclature-case" AND @lecm\\-os:nomenclature\\-case\\-status:"OPEN" AND @lecm\\-os:nomenclature\\-case\\-transient:true'
	});
	model.items = [];
	var i, size;
	if (result && result.length) {
		for (i = 0, size = result.length; i < size; ++i) {
			model.items.push('' + result[i].nodeRef.toString());
		}
	}
})();
