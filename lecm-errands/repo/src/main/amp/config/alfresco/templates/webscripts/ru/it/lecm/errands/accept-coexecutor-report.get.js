model.success = false;

var report = search.findNode(args['nodeRef']);
if (report != null && report.properties["lecm-errands-ts:coexecutor-report-status"] == "ONCONTROL") {
	var document = documentTables.getDocumentByTableDataRow(report);
	var currentEmployee = orgstructure.getCurrentEmployee();
	if (document != null && lecmPermission.hasEmployeeDynamicRole(document, currentEmployee, "ERRANDS_EXECUTOR")) {
		report.properties["lecm-errands-ts:coexecutor-report-status"] = "ACCEPT";
		report.properties["lecm-errands-ts:coexecutor-report-accept-date"] = new Date();
		report.save();

		var recipients = [];
		var coexecutorAssoc = report.assocs["lecm-errands-ts:coexecutor-assoc"];
		if (coexecutorAssoc != null && coexecutorAssoc.length > 0) {
			recipients.push(coexecutorAssoc[0]);
		}

		notifications.sendNotificationFromCurrentUser({
			recipients: recipients,
			templateCode: 'ERRANDS_CO_EXECUTOR_ACCEPT',
			templateConfig: {
				mainObject: document,
				eventExecutor: currentEmployee
			},
			dontCheckAccessToObject: true
		});

		var reportCoexecutor = report.assocs["lecm-errands-ts:coexecutor-assoc"][0];
        var msg = Packages.ru.it.lecm.base.beans.BaseBean.getMessage;
        var logText = msg('ru.it.lecm.errands.bjMessages.acceptCoexecutorReport.message', "#initiator принял отчет соисполнителя  {coexecutor}  по поручению #mainobject");
        logText = logText.replace("{coexecutor}", documentScript.wrapperLink(reportCoexecutor, reportCoexecutor.properties["lecm-orgstr:employee-short-name"]));
		businessJournal.log(document.nodeRef.toString(), "ERRAND_СOEXECUTOR_REPORT", logText, []);

		model.success = true;
	}
}


