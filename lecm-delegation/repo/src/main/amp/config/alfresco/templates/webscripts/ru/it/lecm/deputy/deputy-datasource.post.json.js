<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/filter.lib.js">
(function() {

	var pars = json.get("params");
	var docNodeRefString = args['docNodeRef'];
	var docNodeRef = null;

	if(docNodeRefString) {
		docNodeRef = search.findNode(docNodeRefString);
	}

	var parent = pars.has('parent') ? pars.get('parent') : null;
	var node = null,
		fields = pars.get('fields'),
		nameSubstituteStrings = pars.get('nameSubstituteStrings'),
		children = [],
		deputyAssocs = [];

	if (parent != null && parent != '') {
		node = search.findNode(parent);
		if (node) {
			deputyAssocs = node.assocs['lecm-deputy:deputy-assoc'];
		}
	}

	for each(deputy in deputyAssocs) {
		if(!docNodeRef || deputyService.isDeputyAcceptable(docNodeRef, deputy)) {
			var employeeAssocs = deputy.assocs['lecm-deputy:employee-assoc'];
			if(employeeAssocs) {
				var employee = employeeAssocs[0];
				children.push(employee);
			}
		}
	}
	if(children) {
		model.data = processResults(children, fields, nameSubstituteStrings, 0, children.length);
	}

})();