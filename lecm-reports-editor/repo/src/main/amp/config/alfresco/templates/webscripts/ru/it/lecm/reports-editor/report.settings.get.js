var reportNode = search.findNode(args["reportId"]);
var settingsObj = {};
if (reportNode) {
    var isSubReport = (reportNode.getTypeShort() == "lecm-rpeditor:subReportDescriptor");
    settingsObj["isSub"] = isSubReport;
    settingsObj["path"] = reportNode.getQnamePath();
    settingsObj["parent"] = isSubReport ? reportNode.parent.nodeRef.toString() : args["reportId"];
    var isSQLProvider = false;
    var providerAssoc = reportNode.assocs["lecm-rpeditor:reportProviderAssoc"];
    if (providerAssoc) {
        var index = providerAssoc[0].properties["lecm-rpeditor:reportProviderCode"].indexOf("SQL");
        isSQLProvider = (index >= 0)
    }
    settingsObj["isSQL"] = isSQLProvider;
}

model.settings = settingsObj;