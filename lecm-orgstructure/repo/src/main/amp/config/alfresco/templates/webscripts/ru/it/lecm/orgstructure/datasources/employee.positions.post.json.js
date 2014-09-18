<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
const DEFAULT_MAX_RESULTS = 3000;
function main() {
    var params = {};
    var positionList = [];
    var fields;
    var nameSubstituteStrings;
    var maxResults;
    var sortField = null;
    var sortAsc;
    if (typeof json !== "undefined" && json.has("params")) {
        var pars = json.get("params");

        fields = (pars.get("fields").length() > 0) ? pars.get("fields") : null;
        nameSubstituteStrings = (pars.get("nameSubstituteStrings") !== null) ? pars.get("nameSubstituteStrings") : null;
        maxResults = (pars.get("maxResults") !== null) ? parseInt(pars.get("maxResults"), 10) : DEFAULT_MAX_RESULTS;
        var sort = pars.get("sort");
        if (sort !== null) {
            sortField = sort.split("\\\|")[0];
            sortAsc = (sort.split("\\\|")[1] == "true");
        }

        var employeeRef = (pars.get("parent").length() > 0)  ? pars.get("parent") : null;
        positionList = sortResults(orgstructure.getPositionList(employeeRef), sortField, sortAsc);
    }

    model.data = processResults(positionList, fields, nameSubstituteStrings, 0, positionList.length); // call method from search.lib.js
}

main();
