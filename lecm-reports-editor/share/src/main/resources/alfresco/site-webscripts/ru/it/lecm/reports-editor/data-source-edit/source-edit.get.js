function getDataSource(){
    var typesStr = remote.connect("alfresco").get("/lecm/reports-editor/report-source?reportId=" + page.url.args.reportId);
    if (typesStr.status == 200) {
        return eval("(" + typesStr + ")");
    }
    return null;
}

function createReportDataSource(){
    var connector = remote.connect("alfresco");
    var postBody = {
        reportId: page.url.args.reportId
    };
    var json = connector.post("/lecm/reports-editor/create-report-source", jsonUtils.toJSONString(postBody), "application/json");
    if (json.status == 200) {
        return eval("(" + json + ")");
    }
    return null;
}

var dataSource = getDataSource();
if (!(dataSource && dataSource.nodeRef)) {
    dataSource = createReportDataSource();
}

model.activeSourceId = (dataSource && dataSource.nodeRef) ?  dataSource.nodeRef : null;
