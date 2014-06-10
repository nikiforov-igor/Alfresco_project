var reportsInfos = rptmanager.getRegisteredReports( null, args["forCollection"] ? args["forCollection"] == "true" : false, null, true);
var reports = [];
for (var index= 0; index <= reportsInfos.size()-1; index++) {
    reports.push(reportsInfos.get(index));
}
model.results = reports;

