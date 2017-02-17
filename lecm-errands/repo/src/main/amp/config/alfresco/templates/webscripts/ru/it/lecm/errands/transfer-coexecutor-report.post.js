var reportRefs = [];
var requestContent = eval("(" + requestbody.getContent() + ")");
if (requestContent instanceof Array) {
    reportRefs = requestContent;
}
model.formData = {};
model.success = false;
if (reportRefs && reportRefs.length) {
    var formAttachments = [];
    var formConnections = [];
    var routeDateString = "";
    var document = documentTables.getDocumentByTableDataRow(search.findNode(reportRefs[0]));
    var executionAttachments = document.assocs["lecm-errands:execution-report-attachment-assoc"];
    var documentConnectionsAssoc = document.assocs["lecm-errands:execution-connected-document-assoc"];
    var executionReportText = document.properties["lecm-errands:execution-report"];
    var executionReportStatus = document.properties["lecm-errands:execution-report-status"];
    var formText = executionReportStatus != "PROJECT" ? "" : executionReportText;
    formText += reportRefs.length > 1 ? "Использованы отчеты Соисполнителей:" : "";

    if (executionReportStatus == "PROJECT") {
        if (documentConnectionsAssoc) {
            formConnections = formConnections.concat(documentConnectionsAssoc);
        }
        if (executionAttachments) {
            formAttachments = formAttachments.concat(executionAttachments);
        }
    }
    for (var i = 0; i < reportRefs.length; i++) {
        var report = search.findNode(reportRefs[i]);
        if (report && report.properties["lecm-errands-ts:coexecutor-report-status"] == "ACCEPT") {
            var currentEmployee = orgstructure.getCurrentEmployee();
            if (document && lecmPermission.hasEmployeeDynamicRole(document, currentEmployee, "ERRANDS_EXECUTOR")) {
                var reportCoexecutor = report.assocs["lecm-errands-ts:coexecutor-assoc"][0];
                var reportCoexecutorName = reportCoexecutor.properties["lecm-orgstr:employee-short-name"];
                var reportRouteDate = report.properties["lecm-errands-ts:coexecutor-report-route-date"];
                if (reportRouteDate) {
                    var jsReportRouteDate = new Date(reportRouteDate.getTime());
                    var day = utils.pad(jsReportRouteDate.getDate(), 2);
                    var month = utils.pad(jsReportRouteDate.getMonth() + 1, 2);
                    var year = utils.pad(jsReportRouteDate.getFullYear(), 4);
                    routeDateString = day + "." + month + "." + year;
                }
                var reportText = "\nОтчет Соисполнителя " + reportCoexecutorName + ", направлен " + routeDateString + ":\n";
                if (reportRefs.length == 1 && executionReportStatus != "PROJECT") {
                    reportText = "\nИспользован отчет Соисполнителя " + reportCoexecutorName + ", направлен " + routeDateString + ":\n  ";
                }
                reportText += report.properties["lecm-errands-ts:coexecutor-report-text"] + "\n";
                formText += reportText;

                var attachments = [];
                var reportAttachments = report.assocs["lecm-errands-ts:coexecutor-report-attachment-assoc"];
                if (reportAttachments && reportAttachments.length) {
                    for (var j = 0; j < reportAttachments.length; j++) {
                        var attachmentExist = false;
                        if (executionAttachments && executionAttachments.length) {
                            attachmentExist = executionAttachments.some(function (attachment) {
                                return attachment.equals(reportAttachments[j]);
                            });
                        }
                        if (!attachmentExist || executionReportStatus != "PROJECT") {
                            formAttachments.push(reportAttachments[j]);
                        }
                    }
                }
                var connections = [];
                var reportConnections = report.assocs["lecm-errands-ts:coexecutor-report-connected-document-assoc"];
                if (reportConnections && reportConnections.length) {
                    for (var k = 0; k < reportConnections.length; k++) {
                        var hasAccess = lecmPermission.hasReadAccess(reportConnections[k]);
                        if (!hasAccess) {
                            lecmPermission.pushAuthentication();
                            lecmPermission.setRunAsUserSystem();
                        }
                        var assocExist = false;
                        if (documentConnectionsAssoc && documentConnectionsAssoc.length) {
                            assocExist = documentConnectionsAssoc.some(function (connection) {
                                return connection.equals(reportConnections[k]);
                            });
                        }
                        if (!assocExist || executionReportStatus != "PROJECT") {
                            formConnections.push(reportConnections[k]);
                        }
                        if (!hasAccess) {
                            lecmPermission.popAuthentication();
                        }
                    }
                }
                report.properties["lecm-errands-ts:coexecutor-report-is-transferred"] = true;
                document.save();
                report.save();
            }
        }
    }
    model.formData.formText = formText;
    model.formData.formAttachments = formAttachments.map(function (attachment) {
        return attachment.nodeRef.toString()
    }).join();
    model.formData.formConnections = formConnections.map(function (connection) {
        return connection.nodeRef.toString()
    }).join();
}