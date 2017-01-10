model.success = false;

var report = search.findNode(args['nodeRef']);
var declineReason = args["lecm-errands-ts_coexecutor-report-decline-reason"];
if (report != null && report.properties["lecm-errands-ts:coexecutor-report-status"] == "ONCONTROL") {
    var document = documentTables.getDocumentByTableDataRow(report);
    var currentEmployee = orgstructure.getCurrentEmployee();
    if (document != null && lecmPermission.hasEmployeeDynamicRole(document, currentEmployee, "ERRANDS_EXECUTOR")) {
        report.properties["lecm-errands-ts:coexecutor-report-decline-reason"] = declineReason;
        report.properties["lecm-errands-ts:coexecutor-report-status"] = "DECLINE";
        report.save();

        var recipients = [];
        var coexecutorAssoc = report.assocs["lecm-errands-ts:coexecutor-assoc"];
        if (coexecutorAssoc != null && coexecutorAssoc.length > 0) {
            recipients.push(coexecutorAssoc[0]);
        }

        notifications.sendNotificationFromCurrentUser({
            recipients: recipients,
            templateCode: 'ERRANDS_CO_EXECUTOR_DECLINE',
            templateConfig: {
                mainObject: document,
                eventExecutor: currentEmployee,
                reason: declineReason
            },
            dontCheckAccessToObject: true
        });
        var reportCoexecutor = report.assocs["lecm-errands-ts:coexecutor-assoc"][0];
        var logText = "#initiator ";
        logText += documentScript.wrapperTitle("отклонил", declineReason) + " отчет Cоисполнителя ";
        logText += documentScript.wrapperLink(reportCoexecutor, reportCoexecutor.properties["lecm-orgstr:employee-short-name"]);
        logText += " по поручению #mainobject";
        businessJournal.log(document.nodeRef.toString(), "ERRAND_СOEXECUTOR_REPORT", logText, []);


        model.success = true;
    }
}