(function() {
	function getCountCases(qnamePath, isRecursive) {
		var qnamePathResult = isRecursive ? qnamePath + '/' : qnamePath;
		return searchCounter.query({
			language: 'fts-alfresco',
			query: 'PATH:"/' + qnamePathResult + '/*" AND (+TYPE:"lecm-os:nomenclature-case")'
		});
	}

    var unit = search.findNode(args["nodeRef"]),
        casesCount = 0;
    if (unit) {
	    casesCount = getCountCases(unit.getQnamePath(), true);
    }
    model.hasNomenclatureCases = casesCount > 0;
})();