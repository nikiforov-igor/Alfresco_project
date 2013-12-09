<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/business-journal/datasources/bjEvaluator.lib.js">
    <import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/business-journal/datasources/bjSearch.lib.js">
        <import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
const DEFAULT_MAX_RESULTS = 3000;
function main() {
            var params = {};
        if (typeof json !== "undefined" && json.has("params")) {
            var pars = json.get("params");
            params =
            {
            searchConfig: (pars.get("searchConfig").length() > 0)  ? pars.get("searchConfig") : null,
            sort: (pars.get("sort").length() > 0)  ? pars.get("sort") : null,
            maxResults:(pars.get("maxResults") !== null) ? parseInt(pars.get("maxResults"), 10) : DEFAULT_PAGE_SIZE,
            fields:(pars.get("fields").length() > 0) ? pars.get("fields") : null,
            nameSubstituteStrings:(pars.get("nameSubstituteStrings") !== null) ? pars.get("nameSubstituteStrings") : null,
            showInactive: pars.get("showInactive") == true,
            parent: (pars.get("parent").length() > 0)  ? pars.get("parent") : null,
            searchNodes: (pars.get("searchNodes").length() > 0)  ? pars.get("searchNodes").split(",") : null,
            itemType:(pars.get("itemType").length() > 0)  ? pars.get("itemType") : null,
            startIndex: pars.has("startIndex") ? parseInt(pars.get("startIndex"), 10) : DEFAULT_INDEX,
            filter: pars.has("filter")  ? pars.get("filter") : null
            };
        }

        model.data = getSearchResults(params); // call method from search.lib.js
}


main();
