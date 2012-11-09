<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">

function main() {
    var params = {};
    if (typeof json !== "undefined" && json.has("params")) {
        var pars = json.get("params");
        params =
        {
            term:(pars.get("term") !== null) ? pars.get("term") : null,
            query:(pars.get("query") !== null) ? pars.get("query") : null,
            sort:(pars.get("sort") !== null) ? pars.get("sort") : null,
            maxResults:(pars.get("maxResults") !== null) ? parseInt(pars.get("maxResults"), 10) : DEFAULT_MAX_RESULTS,
            fields:(pars.get("fields") !== null) ? pars.get("fields") : null,
            filter:(pars.get("filter") !== null) ? pars.get("filter") : "",
			fullTextSearch:(pars.get("fullTextSearch") !== null) ? pars.get("fullTextSearch") : ""
        };
    }

    model.data = getSearchResults(params); // call method from search.lib.js
}

main();