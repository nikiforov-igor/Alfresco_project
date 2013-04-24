<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
const DEFAULT_MAX_RESULTS = 3000;
function main() {
    var params = {};
    var members = [];
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
        var searchConfig = (pars.get("sort") != null) ? pars.get("sort") : null;
        if (searchConfig != null && searchConfig != "") {
            var separator = searchConfig.indexOf("|");
            if (separator != -1) {
                asc = (searchConfig.substring(separator + 1) == "true");
                if (searchConfig.indexOf(":") != -1) {
                    sortColumnName = searchConfig.substring(searchConfig.indexOf(":") + 1, separator);
                }
            }
        }

        var parentRef = (pars.get("parent").length() > 0) ? pars.get("parent") : null;
        members = contracts.getMembers(sortColumnName, asc);

    }

    model.data = processResults(members, fields, nameSubstituteStrings, 0, members.length); // call method from search.lib.js
}


main();
