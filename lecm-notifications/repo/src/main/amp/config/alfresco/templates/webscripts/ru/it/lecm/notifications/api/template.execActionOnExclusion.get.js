var templateArg = args["template"];

var templateCode;
var templateNode = search.findNode(templateArg);
if (templateNode) {
    templateCode = templateNode.name;
} else {
    templateCode = templateArg;
}
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
