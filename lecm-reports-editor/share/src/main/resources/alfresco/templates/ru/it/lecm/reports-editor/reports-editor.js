var settings = remote.connect("alfresco").get("/lecm/reports-editor/settings");
if (settings.status == 200) {
    model.settings = settings;
}
if (page.url.args.reportId) {
    var rPath = remote.connect("alfresco").get("/lecm/reports-editor/getReportPath?reportId=" + page.url.args.reportId);
    if (rPath.status == 200) {
        model.reportPath = rPath;
    } else {
        model.reportPath = "";
    }
}
