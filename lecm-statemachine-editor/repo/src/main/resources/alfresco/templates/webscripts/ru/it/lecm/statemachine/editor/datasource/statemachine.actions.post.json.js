<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/substitude.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
const DEFAULT_MAX_RESULTS = 3000;
function main() {
	var params = {};
	var nodes = [];
	var fields;
	var nameSubstituteStrings;
	var maxResults;
	if (typeof json !== "undefined" && json.has("params")) {
		var pars = json.get("params");

		var itemType = pars.get("itemType");
		fields = (pars.get("fields").length() > 0) ? pars.get("fields") : null;
		nameSubstituteStrings = (pars.get("nameSubstituteStrings") !== null) ? pars.get("nameSubstituteStrings") : null;
		maxResults = (pars.get("maxResults") !== null) ? parseInt(pars.get("maxResults"), 10) : DEFAULT_MAX_RESULTS;

		var statusRef = (pars.get("parent").length() > 0)  ? pars.get("parent") : null;
		var status = search.findNode(statusRef);

		if (args["reqType"] != null && args["reqType"] == "userTransition") {
			var action = getActionByType(status, "lecm-stmeditor:FinishStateWithTransition", "take");
			nodes = action.getChildAssocsByType(itemType);
		}

	}

	if (nodes != null) {
		model.data = processResults(nodes, fields, nameSubstituteStrings, maxResults); // call method from search.lib.js
	}

}

function getActionByType(status, actionType, execution) {
	var children = status.children;
	var result = null;
	for each (var child in children) {
		if (child.getTypeShort() == actionType && child.properties["lecm-stmeditor:actionExecution"] == execution) {
			result = child;
		}
	}
	return result;
}

main();
