var report = search.findNode(args["reportId"]);
if (report){
    var assocs = report.getChildAssocsByType("lecm-rpeditor:reportDataSource");
    if (assocs) {
        model.dataSource = assocs[0] ? assocs[0] : null;
    }
}
