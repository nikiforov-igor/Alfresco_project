function getDataSource(){
    var typesStr = remote.connect("alfresco").get("/lecm/reports-editor/report-source?reportId=" + args["reportId"]);
    if (typesStr.status == 200) {
        var source = eval ("(" + typesStr + ")");
        return source;
    }
    return null;
}

var dataSource = getDataSource();
model.activeSourceId = (dataSource && dataSource.nodeRef) ?  dataSource.nodeRef : null;


