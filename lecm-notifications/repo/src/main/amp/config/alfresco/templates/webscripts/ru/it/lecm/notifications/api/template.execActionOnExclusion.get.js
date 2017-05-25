<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/notifications/api/getTemplateCode.lib.js">

var templateCode = getTemplateCode(args["template"]);
if (url.templateArgs.action) {
    var action = ('' + url.templateArgs.action);
    if ("create" == action) {
        model.success = notifications.createExclusionForEmployess(templateCode, args["forEmployees"] ? args["forEmployees"].split(",") : []);
    } else if ("delete" == action) {
        model.success = notifications.deleteExclusionForEmployess(templateCode, args["forEmployees"] ? args["forEmployees"].split(",") : []);
    } else if ("clearAll" == action) {
        model.success = notifications.clearExclusions(templateCode);
    }
} else {
    status.setCode(500, "Not available action");
    model.success = false;
}
