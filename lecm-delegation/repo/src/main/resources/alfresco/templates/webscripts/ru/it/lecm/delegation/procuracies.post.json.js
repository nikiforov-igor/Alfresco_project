<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/search/search.post.json.js">
//пробегаемся по результирующему списку доверенностей и выгоняем оттуда те, бизнес роли которых active=false
/*
logger.log ("*********************************************************************************************");
for (var prop in model) {
	logger.log (prop);
}
if (model.data) {
	logger.log ("model.data = " + jsonUtils.toJSONString (model.data));
}
logger.log ("*********************************************************************************************");
*/
var items = model.data.items;
var activeItems = [];
for (var i = 0; i < items.length; ++i) {
	var item = items[i];
	var procuracy = item.node;
	var businessRoleAssocs = procuracy.assocs["lecm-d8n:procuracy-business-role-assoc"];
	if (businessRoleAssocs) {
		var businessRole = businessRoleAssocs[0]; //так как одна доверенность - одна бизнес роль
		var nodeRef = businessRole.nodeRef;
		var isActive = businessRole.properties["lecm-dic:active"];
		var name = businessRole.name;
		var activity = (isActive)?"is active!":"is not active!";
		logger.log ("[" + nodeRef + "]" + name + " " + activity + "(active=" + isActive + ")");
//		for (var property in businessRole.properties) {
//			logger.log ("\t" + property + " = " + businessRole.properties[property]);
//		}
		if (isActive) {
			activeItems.push (item);
		}
	}
}
model.data.items = activeItems;
