<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/notifications/api/getTemplateCode.lib.js">

var templateCode = getTemplateCode(json.get("template"));
model.success = notifications.subscribeOnTemplate(templateCode, json.has("employee") ? search.findNode(json.get("employee")) : null);