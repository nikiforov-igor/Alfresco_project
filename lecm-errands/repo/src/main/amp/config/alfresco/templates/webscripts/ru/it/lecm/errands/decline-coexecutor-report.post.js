model.success = false;

var report = search.findNode(args['nodeRef']);
var requestContent = eval("(" + requestbody.getContent() + ")");
var declineReason = requestContent["prop_lecm-errands-ts_coexecutor-report-decline-reason"];
if (report && report.properties["lecm-errands-ts:coexecutor-report-status"] == "ONCONTROL") {
    var document = documentTables.getDocumentByTableDataRow(report);
    var currentEmployee = orgstructure.getCurrentEmployee();
    if (document && lecmPermission.hasEmployeeDynamicRole(document, currentEmployee, "ERRANDS_EXECUTOR")) {
        report.properties["lecm-errands-ts:coexecutor-report-decline-reason"] = declineReason;
        report.properties["lecm-errands-ts:coexecutor-report-status"] = "DECLINE";
        report.save();

        var recipients = [];
        var coexecutorAssoc = report.assocs["lecm-errands-ts:coexecutor-assoc"];
        if (coexecutorAssoc && coexecutorAssoc.length > 0) {
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
        var msg = Packages.ru.it.lecm.base.beans.BaseBean.getMessage;
        var logText = msg('ru.it.lecm.errands.bjMessages.declineCoexecutorReport.message', "#initiator {decline} отчет Cоисполнителя  {coexecutor}  по поручению #mainobject");
        logText = logText.replace("{coexecutor}", documentScript.wrapperLink(reportCoexecutor, reportCoexecutor.properties["lecm-orgstr:employee-short-name"]));
        logText = logText.replace("{decline}", documentScript.wrapperTitle(msg('ru.it.lecm.errands.bjMessages.declineCoexecutorReport.declineParamText', "отклонил"), declineReason));
        businessJournal.log(document.nodeRef.toString(), "ERRAND_СOEXECUTOR_REPORT", logText, []);

        model.success = true;
    }
}