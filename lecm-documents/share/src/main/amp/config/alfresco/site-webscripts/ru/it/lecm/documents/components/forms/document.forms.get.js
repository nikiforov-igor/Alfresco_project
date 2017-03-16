function getReports(type) {
    var reports = [];

    var url = '/lecm/reports/rptmanager/registeredReports?forCollection=false' + (type ? ('&docType=' + type) : "");
    var result = remote.connect("alfresco").get(stringUtils.urlEncodeComponent(url));
    if (result.status == 200) {
        var json = jsonUtils.toObject(result);
        var reportsList = json && json.list || [];
        for (var index in reportsList) {
            reports.push(reportsList[index]);
        }
    }

    return reports;
}

function main() {
    model.nodeRef = args["nodeRef"];

    var url = '/api/metadata?nodeRef=' + model.nodeRef;
    var result = remote.connect("alfresco").get(url);
    if (result.status == 200) {
        var metadata = eval('(' + result + ')');
        if (metadata && (metadata.type)) {
            model.documentType = metadata.type;

            model.reportsDescriptors = getReports(metadata.type);
        }
    }
}

main();
