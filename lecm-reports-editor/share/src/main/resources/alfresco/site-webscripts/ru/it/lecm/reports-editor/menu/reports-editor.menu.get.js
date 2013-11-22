if (page.url.args.reportId) {
    var setStr = remote.connect("alfresco").get("/lecm/reports-editor/getReportSettings?reportId=" + page.url.args.reportId);
    if (setStr.status == 200) {
        model.reportSettings = eval ("(" + setStr + ")");
    }
}
