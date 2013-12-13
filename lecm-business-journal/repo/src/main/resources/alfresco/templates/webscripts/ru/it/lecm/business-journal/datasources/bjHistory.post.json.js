<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/business-journal/datasources/bjEvaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/business-journal/datasources/bjSearch.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
const DEFAULT_MAX_RESULTS = 3000;
function main() {
    var params = {};
    var groups = [];
    var fields;
    var nameSubstituteStrings;
    var maxResults;

    if (typeof json !== "undefined" && json.has("params")) {
        var pars = json.get("params");

        var showInactive = ((pars.get("showInactive") !== null) && (pars.get("showInactive")+"" == "true"))  ? true : false;
        fields = (pars.get("fields").length() > 0) ? pars.get("fields") : null;
        nameSubstituteStrings = (pars.get("nameSubstituteStrings") !== null) ? pars.get("nameSubstituteStrings") : null;
        maxResults = (pars.get("maxResults") !== null) ? parseInt(pars.get("maxResults"), 10) : DEFAULT_MAX_RESULTS;

        var sortColumnName = "";
        var asc = false;
        var sort = (pars.get("sort") != null) ? pars.get("sort") : null;
        if (sort != null && sort != "") {
            var separator = sort.indexOf("|");
            if (separator != -1) {
                asc = (sort.substring(separator + 1) == "true");
                sortColumnName = sort.substring(0, separator);
            }
        }

        var showSecondary = false;
        var searchConfigString = (pars.get("searchConfig").length() > 0)  ? pars.get("searchConfig") : null;
        if (searchConfigString != null && searchConfigString.length() > 0) {
            var searchConfig = jsonUtils.toObject(searchConfigString);
            showSecondary = searchConfig.showSecondary != null ? searchConfig.showSecondary : false;
        }

        var parentRef = (pars.get("parent").length() > 0) ? pars.get("parent") : null;
        groups = businessJournal.getHistory(parentRef, sortColumnName, asc, showSecondary, showInactive);
    }

    model.data = processResults(groups, fields, nameSubstituteStrings, 0, groups.length); // call method from search.lib.js
}


main();
