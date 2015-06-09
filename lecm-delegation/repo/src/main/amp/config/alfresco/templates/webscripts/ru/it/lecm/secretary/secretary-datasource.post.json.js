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
	secAssocs = null;

	secAssocs = node.sourceAssocs['lecm-secretary-aspects:chief-assoc'];

	for each(secAssoc in secAssocs) {
		children.push(secAssoc);
	}
	if(children) {
		model.data = processResults(children, fields, nameSubstituteStrings, 0, children.length);
	}

})();