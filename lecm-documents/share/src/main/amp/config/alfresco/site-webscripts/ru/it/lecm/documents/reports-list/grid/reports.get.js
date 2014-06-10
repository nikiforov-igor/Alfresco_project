function getReports(type) {
    var reports = [];

    var url = '/lecm/reports/rptmanager/registeredReports?forCollection=true' + (type ? ('&docType=' + type) : "");
    var result = remote.connect("alfresco").get(url);

    if (result.status == 200) {
        var json = jsonUtils.toObject(result);
        var reportsList = json.list.toArray();
        for (var index in reportsList) {
            reports.push(reportsList[index]);
        }
    }

    return reports;
}


function main() {
    model.itemType = args["itemType"];
    model.reportsDescriptors = getReports(args["itemType"]);
}

main();
