(function() {
	function getCountCases(qnamePath, isRecursive) {
		var qnamePathResult = isRecursive ? qnamePath + '/' : qnamePath;
		return searchCounter.query({
			language: 'fts-alfresco',
			query: 'PATH:"/' + qnamePathResult + '/*" AND (+TYPE:"lecm-os:nomenclature-case")'
		});
	}

    var unit = search.findNode(args['nodeRef']),
        casesPresent = false,
	    nomenclatureUnitSections = [];
    if (unit) {
	    nomenclatureUnitSections = unit.sourceAssocs['lecm-os:nomenclature-unit-section-unit-assoc'];
	    casesPresent = nomenclatureUnitSections.some(function (nomenclatureUnitSection) {
	        return getCountCases(nomenclatureUnitSection.getQnamePath(), true) > 0;
	    });
    }
    model.hasNomenclatureCases = casesPresent;
})();