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

function listTemplates(){
    var sourcesStr = remote.connect("alfresco").get("/lecm/reports-editor/templates");
    if (sourcesStr.status == 200) {
        return eval ("(" + sourcesStr + ")");
    }
    return [];
}

function isExistInRepo(testName){
    var templatesList = listTemplates();
    for (var index in templatesList) {
        if (templatesList[index].name == testName){
            return true;
        }
    }
    return false;
}

var template = getReportTemplate();

model.reportTypes = getTypes();
model.activeTemplateId = (template && template.nodeRef) ? template.nodeRef : null;
model.existInRepo = (template && template.name) ? isExistInRepo(template.name) : false;
