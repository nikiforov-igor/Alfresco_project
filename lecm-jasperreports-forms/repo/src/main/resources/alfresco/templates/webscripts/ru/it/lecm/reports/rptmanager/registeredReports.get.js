var reportsInfos = rptmanager.getRegisteredReports( args["docType"], args["reportType"]);
var reports = [];
for (var key in reportsInfos) {
    var rInfo = reportsInfos[key];
    reports.push({
        "reportName":rInfo.reportName,
        "reportCode":rInfo.reportCode
    });
}
model.data = reports;
