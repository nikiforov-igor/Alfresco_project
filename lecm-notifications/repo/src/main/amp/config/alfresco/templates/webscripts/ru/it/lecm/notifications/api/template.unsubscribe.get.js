<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/notifications/api/getTemplateCode.lib.js">

var templateCode = getTemplateCode(args["template"]);
model.success = notifications.unsubscribeFromTemplate(templateCode, args["employee"] ? search.findNode(args["employee"]) : null);