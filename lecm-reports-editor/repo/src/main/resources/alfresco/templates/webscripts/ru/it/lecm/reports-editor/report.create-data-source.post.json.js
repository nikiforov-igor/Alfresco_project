if (typeof json !== "undefined") {
    var reportRef = json.get("reportId");
    if (reportRef) {
        var report = search.findNode(reportRef);
        if (report) {
            model.dataSource = report.createNode(null, "lecm-rpeditor:reportDataSource");
        }
    }
}