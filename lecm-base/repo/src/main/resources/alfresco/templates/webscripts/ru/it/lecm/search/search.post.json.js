<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
const DEFAULT_MAX_RESULTS = 3000;

function main() {
    var params = {};
    if (typeof json !== "undefined" && json.has("params")) {
        var pars = json.get("params");
        params =
        {
            searchConfig: (pars.get("searchConfig").length() > 0)  ? pars.get("searchConfig") : null,
            maxResults:(pars.get("maxResults") !== null) ? parseInt(pars.get("maxResults"), 10) : DEFAULT_MAX_RESULTS,
            fields:(pars.get("fields").length() > 0) ? pars.get("fields") : null,
            showInactive: pars.get("showInactive") == true,
            parent: (pars.get("parent").length() > 0)  ? pars.get("parent") : null,
            itemType:(pars.get("itemType").length() > 0)  ? pars.get("itemType") : null,
        };
    }

    model.data = getSearchResults(params); // call method from search.lib.js
}

main();