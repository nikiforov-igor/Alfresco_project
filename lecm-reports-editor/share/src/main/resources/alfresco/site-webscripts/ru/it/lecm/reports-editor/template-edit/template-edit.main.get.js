function getTypes(){
    var typesStr = remote.connect("alfresco").get("/lecm/reports-editor/report-types");
    if (typesStr.status == 200) {
        return eval ("(" + typesStr + ")");
    }
    return [];
}

function getReportTemplateId(){
    var typesStr = remote.connect("alfresco").get("/lecm/reports-editor/report-template?reportId=" + page.url.args.reportId);
    if (typesStr.status == 200) {
        var template = eval ("(" + typesStr + ")");
        return template.nodeRef;
    }
    return null;
}

model.reportTypes = getTypes();
model.activeTemplateId = getReportTemplateId();
