(function() {
	function getCountCases(qnamePath, isRecursive) {
		var qnamePathResult = isRecursive ? qnamePath + '/' : qnamePath;
		return searchCounter.query({
			language: 'fts-alfresco',
			query: 'PATH:"/' + qnamePathResult + '/*" AND (+TYPE:"lecm-os:nomenclature-case")'
		});
	}

    var unit = search.findNode(args['nodeRef']),
        casesCount = 0,
	    nomenclatureUnitSections = [],
	    nomenclatureUnitSection;
    if (unit) {
	    nomenclatureUnitSections = unit.sourceAssocs['lecm-os:nomenclature-unit-section-unit-assoc'];
	    for each (nomenclatureUnitSection in nomenclatureUnitSections) {
		    casesCount += getCountCases(nomenclatureUnitSection.getQnamePath(), true);
	    }
    }
    model.hasNomenclatureCases = casesCount > 0;
})();