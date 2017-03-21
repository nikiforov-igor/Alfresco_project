(function() {
	function getCountCases(qnamePath, isRecursive) {
		var qnamePathResult = isRecursive ? qnamePath + '/' : qnamePath;
		return searchCounter.query({
			language: 'fts-alfresco',
			query: 'PATH:"/' + qnamePathResult + '/*" AND (+TYPE:"lecm-os:nomenclature-case")'
		});
	}

	function hasCases(nomenclatureUnitSection) {
		return getCountCases(nomenclatureUnitSection.getQnamePath(), true) > 0;
	}

    var unit = search.findNode(args['nodeRef']),
	    nomenclatureUnitSections = [];
    if (unit) {
	    nomenclatureUnitSections = unit.sourceAssocs['lecm-os:nomenclature-unit-section-unit-assoc'];
    }
    model.hasNomenclatureCases = nomenclatureUnitSections.some(hasCases);
})();