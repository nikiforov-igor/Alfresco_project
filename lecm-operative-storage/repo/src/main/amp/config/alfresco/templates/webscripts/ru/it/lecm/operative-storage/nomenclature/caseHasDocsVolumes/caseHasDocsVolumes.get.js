(function() {
	var cases = args['items'].split(',');
	var checkVolumes = ("true" == args['checkVolumes']);

	for each(caseEl in cases) {
		if(model.result = operativeStorage.caseHasDocsOrVolumes(caseEl, checkVolumes)) {
			return;
		}
	}
	model.result = false;
})();