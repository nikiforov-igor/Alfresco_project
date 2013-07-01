model.settings = remote.connect("alfresco").get("/lecm/reports-editor/settings");
if (page.url.args.reportId) {
    model.reportPath = remote.connect("alfresco").get("/lecm/reports-editor/getReportPath?reportId=" + page.url.args.reportId);
}
