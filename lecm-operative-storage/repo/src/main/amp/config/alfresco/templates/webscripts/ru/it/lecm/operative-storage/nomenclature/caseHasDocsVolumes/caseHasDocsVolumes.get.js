(function() {
	var cases = args['items'].split(',');
	for each(caseEl in cases) {
		if(model.result = operativeStorage.caseHasDocsOrVolumes(caseEl)) {
			return;
		}
	}
	model.result = false;
})();