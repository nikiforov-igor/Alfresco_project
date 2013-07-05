function getDataSource(){
    var typesStr = remote.connect("alfresco").get("/lecm/reports-editor/report-source?reportId=" + page.url.args.reportId);
    if (typesStr.status == 200) {
        var source = eval ("(" + typesStr + ")");
        return source;
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
        var source = eval ("(" + json + ")");
        return source;
    }
    return null;
}

function listSources(){
    var sourcesStr = remote.connect("alfresco").get("/lecm/reports-editor/data-sources");
    if (sourcesStr.status == 200) {
        return eval ("(" + sourcesStr + ")");
    }
    return [];
}

function isExistInRepo(testSourceCode){
    var sourcesList = listSources();
    for (var index in sourcesList) {
        if (sourcesList[index].name == testSourceCode){
            return true;
        }
    }
    return false;
}

var dataSource = getDataSource();
if (dataSource.nodeRef) {
    model.existInRepo = dataSource.name ? isExistInRepo(dataSource.name) : false;
} else {
    dataSource = createReportDataSource();
    model.existInRepo = false;
}

model.activeSourceId = (dataSource && dataSource.nodeRef) ?  dataSource.nodeRef : null;
