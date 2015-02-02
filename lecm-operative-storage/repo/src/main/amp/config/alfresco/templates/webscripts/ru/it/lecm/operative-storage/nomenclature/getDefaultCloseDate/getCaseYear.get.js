(function(){

	var yearSection = operativeStorage.getYearSection(search.findNode(args['nodeRef']));
	model.date = yearSection.properties['lecm-os:nomenclature-year-section-year'] + '-12-31';

})();


