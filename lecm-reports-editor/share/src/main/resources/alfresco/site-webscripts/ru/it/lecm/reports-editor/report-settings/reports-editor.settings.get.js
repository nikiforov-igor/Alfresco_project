if (args.reportId) {
    var setStr = remote.connect("alfresco").get("/lecm/reports-editor/getReportSettings?reportId=" + args.reportId);
    if (setStr.status == 200) {
        model.reportSettings = setStr;
    }
}