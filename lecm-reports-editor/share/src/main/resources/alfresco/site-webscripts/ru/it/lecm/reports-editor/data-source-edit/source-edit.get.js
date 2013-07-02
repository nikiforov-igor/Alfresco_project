function getDataSource(){
    var typesStr = remote.connect("alfresco").get("/lecm/reports-editor/report-source?reportId=" + page.url.args.reportId);
    if (typesStr.status == 200) {
        var source = eval ("(" + typesStr + ")");
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
model.activeSourceId = (dataSource && dataSource.nodeRef) ?  dataSource.nodeRef : null;

model.existInRepo = (dataSource && dataSource.name) ? isExistInRepo(dataSource.name) : false;

