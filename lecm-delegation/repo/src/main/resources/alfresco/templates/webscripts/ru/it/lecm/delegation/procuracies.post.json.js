<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/substitude.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">
const DEFAULT_MAX_RESULTS = 3000;

function main() {
    var params = {};
	logger.log ("***********************************************************************************************************************");
	logger.log ("json = " + jsonUtils.toJSONString (json));
	logger.log ("***********************************************************************************************************************");
    if (typeof json !== "undefined" && json.has("params")) {
        var pars = json.get("params");
        params = {
            searchConfig: (pars.get("searchConfig").length() > 0)  ? pars.get("searchConfig") : null,
            maxResults:(pars.get("maxResults") !== null) ? parseInt(pars.get("maxResults"), 10) : DEFAULT_MAX_RESULTS,
            fields:(pars.get("fields").length() > 0) ? pars.get("fields") : null,
			nameSubstituteStrings:(pars.get("nameSubstituteStrings") !== null) ? pars.get("nameSubstituteStrings") : null,
			showInactive: pars.get("showInactive") == true,
            parent: (pars.get("parent").length() > 0)  ? pars.get("parent") : null, //delegation-opts который есть у каждого employee
            itemType:(pars.get("itemType").length() > 0)  ? pars.get("itemType") : null //тип данных которые там хранятся
        };
    }
	logger.log ("***********************************************************************************************************************");
	logger.log ("params = " + jsonUtils.toJSONString (params));
	logger.log ("***********************************************************************************************************************");
    model.data = getSearchResults(params); // call method from search.lib.js
	//далее мы сюда в конец дописываем бизнес роли которые есть у этого сотрудника
}

main();