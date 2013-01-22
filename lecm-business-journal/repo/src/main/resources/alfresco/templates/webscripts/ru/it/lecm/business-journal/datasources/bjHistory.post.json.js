<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
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

        fields = (pars.get("fields").length() > 0) ? pars.get("fields") : null;
        nameSubstituteStrings = (pars.get("nameSubstituteStrings") !== null) ? pars.get("nameSubstituteStrings") : null;
        maxResults = (pars.get("maxResults") !== null) ? parseInt(pars.get("maxResults"), 10) : DEFAULT_MAX_RESULTS;

        var sortColumnName = "";
        var asc = false;
        var searchConfig = (pars.get("searchConfig") != null) ? pars.get("searchConfig") : null;
        if (searchConfig != null && searchConfig != "") {
            var searchConfigObject = eval("(" + searchConfig + ")");

            if (searchConfigObject != null && searchConfigObject.sort != null && searchConfigObject.sort.length > 0) {
                var separator = searchConfigObject.sort.indexOf("|");
                if (separator != -1) {
                    asc = (searchConfigObject.sort.substring(separator + 1) == "true");

                    if (searchConfigObject.sort.indexOf(":") != -1) {
                        sortColumnName = searchConfigObject.sort.substring(searchConfigObject.sort.indexOf(":") + 1, separator);
                    }
                }
            }
        }

        var parentRef = (pars.get("parent").length() > 0) ? pars.get("parent") : null;
        groups = businessJournal.getHistory(parentRef, sortColumnName, asc);
    }

    model.data = processResults(groups, fields, nameSubstituteStrings, maxResults); // call method from search.lib.js
}


main();
