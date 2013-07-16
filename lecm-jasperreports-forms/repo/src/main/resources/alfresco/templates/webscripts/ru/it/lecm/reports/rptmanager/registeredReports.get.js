var reportsInfos = rptmanager.getRegisteredReports( args["docType"], args["reportType"]);
var reports = [];
for (var index= 0; index <= reportsInfos.size()-1; index++) {
    reports.push(reportsInfos.get(index));
}
model.data = reports;
