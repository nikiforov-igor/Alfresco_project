<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/filter.lib.js">
(function() {

	var pars = json.get("params");

	var node = search.findNode(pars.get('parent')),
	fields = pars.get('fields'),
	nameSubstituteStrings = pars.get('nameSubstituteStrings'),
	children = [],
	deputyAssocs = null;

	deputyAssocs = node.assocs['lecm-deputy:deputy-assoc'];

	for each(deputy in deputyAssocs) {
		var subjectAssocs = deputy.assocs['lecm-deputy:subject-assoc'];
		if(subjectAssocs && subjectAssocs.length > 0) {
			children.push(deputy);
		}
	}

	if(children) {
		model.data = processResults(children, fields, nameSubstituteStrings, 0, children.length);
	}

})();