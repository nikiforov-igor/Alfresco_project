<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/workflow/review/datasources/reviewEvaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/filter.lib.js">

var children,
	pars = json.get("params"),
	fields = pars.get('fields'),
	parent = pars.get('parent'),
	reviewInfo = utils.getNodeFromString(parent),
	children = reviewInfo.sourceAssocs['lecm-review-info:info-assoc'],
	nameSubstituteStrings = pars.get('nameSubstituteStrings');

model.data = processResults(children, fields, nameSubstituteStrings, 0, children.length);
