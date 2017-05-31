<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/notifications/api/getTemplateCode.lib.js">

var templateCode = getTemplateCode(json.get("template"));
model.success = notifications.createExclusionForEmployess(templateCode, json.has("forEmployees") ? ("" + json.get("forEmployees")).split(",") : []);