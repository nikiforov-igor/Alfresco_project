<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/substitude.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/parse-args.lib.js">

const DEFAULT_MAX_RESULTS = 3000;

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
var searchResults = getSearchResults(params); // call method from search.lib.js
var searchItems = searchResults.items; //массивчик с данными по таблице procuracy
//далее мы сюда в конец дописываем бизнес роли которые есть у этого сотрудника
//получаем бизнес роли по параметрам делегирования
var businessRoles = delegation.getUniqueBusinessRolesByDelegationOpts (params.parent);
//пробегаемся по списку доверенностей, по ассоциациям достаем бизнес роли и проверяем есть ли они с списке уникальных
//если роли есть то из списка уникальных удаляем их
for (var i = 0; i < searchItems.length; ++i) {
	var procuracy = searchItems[i].node;
	var businessRoleAssocs = procuracy.assocs["lecm-d8n:procuracy-business-role-assoc"];
	if (businessRoleAssocs) {
		var businessRole = businessRoleAssocs[0]; //так как одна доверенность - одна бизнес роль
		var index = businessRoles.indexOf (businessRole);
		if (index >= 0) { //если роль нашлась то грохнем ее
			businessRoles.splice (index, 1);
			logger.log ("splice");
		}
	}
}
searchResults.paging.totalRecords += businessRoles.length;
//для оставшихся бизнес ролей формируем объектик который у нас отправится в шаблон
var fields = params.fields.split(",");
logger.log ("fields.length = " + fields.length);
for (var i = 0; i < businessRoles.length; ++i) {
	var businessRole = businessRoles[i];
	logger.log ("businessRole = " + businessRole.nodeRef);
	var item = {
		"actionPermissions": {
			"create": true,
			"edit": true,
			"delete": true
		},
		"createdBy": {
			"userName": "",
			"firstName": "",
			"lastName": "",
			"displayName": ""
		},
		"modifiedBy": {
			"userName": "",
			"firstName": "",
			"lastName": "",
			"displayName": ""
		},
		"actionLabels": {},
		"tags": [],
		"actionSet": "",
		"node": {
			"nodeRef": "",
			"properties": {
				"created": new Date (),
				"modified": new Date ()
			}
		},
		"nodeData": {}
	};
	logger.log ("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	for (var j = 0; j < fields.length; ++j) {
		var field = "assoc_" + fields[j];
		logger.log ("field = " + field);
		var nodeData = {
			"type": "text",
			"displayValue": "",
			"value": ""
		};
		item.nodeData[field] = nodeData;
	}
	logger.log ("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	logger.log ("***********************************************************************************************************************");
	logger.log ("nodeData = " + jsonUtils.toJSONString (item.nodeData));
	logger.log ("***********************************************************************************************************************");
	item.nodeData["assoc_lecm-d8n_procuracy-business-role-assoc"].displayValue = businessRole.name;
	item.nodeData["assoc_lecm-d8n_procuracy-business-role-assoc"].value = businessRole.nodeRef.toString();
	searchItems.push (item);
}


model.data = searchResults;
