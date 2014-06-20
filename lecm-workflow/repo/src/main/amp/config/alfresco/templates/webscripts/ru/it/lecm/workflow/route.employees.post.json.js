<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
const DEFAULT_MAX_RESULTS = 3000;
function main() {
    var params = {};
    var assigners = [];
    var fields;
    var nameSubstituteStrings;
    var maxResults;
    if (typeof json !== "undefined" && json.has("params")) {
        var pars = json.get("params");

        fields = (pars.get("fields").length() > 0) ? pars.get("fields") : null;
        nameSubstituteStrings = (pars.get("nameSubstituteStrings") !== null) ? pars.get("nameSubstituteStrings") : null;
        maxResults = (pars.get("maxResults") !== null) ? parseInt(pars.get("maxResults"), 10) : DEFAULT_MAX_RESULTS;

        var routeRef = (pars.get("parent").length() > 0)  ? pars.get("parent") : null;
        assigners = lecmWorkflowService.getRouteEmployess(routeRef, args["workflowType"]);
    }

    model.data = processResults(assigners, fields, nameSubstituteStrings, 0, assigners.length); // call method from search.lib.js
}

main();
