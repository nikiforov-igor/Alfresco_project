var reportNode = search.findNode(args["reportId"]);
var settingsObj = {};
if (reportNode) {
    var isSubReport = reportNode.properties["lecm-rpeditor:reportIsSubReport"];
    settingsObj["isSub"] = isSubReport;
    settingsObj["path"] = reportNode.getQnamePath();
    settingsObj["parent"] = isSubReport ? reportNode.parent.nodeRef.toString() : args["reportId"];

}

model.settings = settingsObj;