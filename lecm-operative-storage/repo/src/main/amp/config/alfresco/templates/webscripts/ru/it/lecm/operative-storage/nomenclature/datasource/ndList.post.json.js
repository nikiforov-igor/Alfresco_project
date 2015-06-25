<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/filter.lib.js">

const DEFAULT_PAGE_SIZE = 20;
const DEFAULT_INDEX = 0;

(function () {

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
            useChildQuery: pars.has("useChildQuery") ? ("" + pars.get("useChildQuery") == "true") : false,
            useFilterByOrg: pars.has("useFilterByOrg") ? ("" + pars.get("useFilterByOrg") == "true") : true,
            useOnlyInSameOrg: pars.has("useOnlyInSameOrg") ? ("" + pars.get("useOnlyInSameOrg") == "true") : false,
            filter: pars.has("filter")  ? pars.get("filter") : null
        };
    }

    var tmp = [
        {
            query: '{{FILTER_YEARS_BY_ORG({allowAdmin: true})}}'
        }
    ];

    params.filter = jsonUtils.toJSONString(tmp);
    if(!params.searchConfig || params.searchConfig.length == 0) {
        logger.warn(params.searchConfig);
        tmp = {
            formData: {}
        };
        params.searchConfig = jsonUtils.toJSONString(tmp);
    }

    model.data = getSearchResults(params);

})();