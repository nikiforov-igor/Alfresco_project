model.success = false;

var report = search.findNode(args['nodeRef']);
if (report != null && report.properties["lecm-errands-ts:coexecutor-report-status"] == "ONCONTROL") {
	var document = documentTables.getDocumentByTableDataRow(report);
	var currentEmployee = orgstructure.getCurrentEmployee();
	if (document != null && lecmPermission.hasEmployeeDynamicRole(document, currentEmployee, "ERRANDS_EXECUTOR")) {
		report.properties["lecm-errands-ts:coexecutor-report-status"] = "ACCEPT";
		report.properties["lecm-errands-ts:coexecutor-report-accept-date"] = new Date();
		report.save();

		var notificationText = documentScript.wrapperLink(currentEmployee, currentEmployee.properties["lecm-orgstr:employee-short-name"]);
		notificationText +=  " принял ваш отчет по поручению ";
		notificationText += documentScript.wrapperDocumentLink(document, "{lecm-document:present-string}");

		var recipients = [];
		var coexecutorAssoc = report.assocs["lecm-errands-ts:coexecutor-assoc"];
		if (coexecutorAssoc != null && coexecutorAssoc.length > 0) {
			recipients.push(coexecutorAssoc[0]);
		}

		notifications.sendNotificationFromCurrentUser(recipients, notificationText, document, true);

		model.success = true;
	}
}


