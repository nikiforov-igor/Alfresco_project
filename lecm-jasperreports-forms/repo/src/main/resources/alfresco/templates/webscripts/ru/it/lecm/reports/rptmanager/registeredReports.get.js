var reportsInfos = rptmanager.getRegisteredReports( args["docType"], args["reportType"]);
var reports = [];
for (var index= 0; index <= reportsInfos.size()-1; index++) {
    var report = reportsInfos.get(index);
    reports.push({
        "reportName":report.reportName,
        "reportCode":report.reportCode
    });
}
model.data = reports;
