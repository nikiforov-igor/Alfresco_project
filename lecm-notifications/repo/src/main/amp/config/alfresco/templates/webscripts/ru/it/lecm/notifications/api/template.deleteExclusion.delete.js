<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/notifications/api/getTemplateCode.lib.js">

var templateCode = getTemplateCode(args["template"]);
model.success = notifications.deleteExclusionForEmployess(templateCode, args["forEmployees"] ? args["forEmployees"].split(",") : []);
