model.success = false;

var report = search.findNode(args['nodeRef']);
if (report != null && report.properties["lecm-errands-ts:coexecutor-report-status"] == "ONCONTROL") {
	var document = documentTables.getDocumentByTableDataRow(report);
	var currentEmployee = orgstructure.getCurrentEmployee();
	if (document != null && lecmPermission.hasEmployeeDynamicRole(document, currentEmployee, "ERRANDS_EXECUTOR")) {
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
				eventExecutor: currentEmployee
			},
			dontCheckAccessToObject: true
		});

		model.success = true;
	}
}