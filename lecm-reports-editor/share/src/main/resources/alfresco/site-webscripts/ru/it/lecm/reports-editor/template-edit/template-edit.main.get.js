function getTypes(){
    var typesStr = remote.connect("alfresco").get("/lecm/reports-editor/report-types");
    if (typesStr.status == 200) {
        return eval ("(" + typesStr + ")");
    }
    return [];
}

function getReportTemplate(){
    var typesStr = remote.connect("alfresco").get("/lecm/reports-editor/report-template?reportId=" + page.url.args.reportId);
    if (typesStr.status == 200) {
        var template = eval ("(" + typesStr + ")");
        return template;
    }
    return null;
}

var template = getReportTemplate();

model.reportTypes = getTypes();
model.activeTemplateId = (template && template.nodeRef) ? template.nodeRef : null;
