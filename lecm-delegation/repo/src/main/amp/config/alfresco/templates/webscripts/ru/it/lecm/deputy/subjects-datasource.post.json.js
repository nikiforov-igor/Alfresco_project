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
	subjectsAssoc = null;

	subjectsAssoc = node.assocs['lecm-deputy:subject-assoc'];

	if(subjectsAssoc) {
		model.data = processResults(subjectsAssoc, fields, nameSubstituteStrings, 0, children.length);
	}

})();