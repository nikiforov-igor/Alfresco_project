var reportRefs = [];
var requestContent = eval("(" + requestbody.getContent() + ")");
if (requestContent instanceof Array) {
    reportRefs = requestContent;
}
model.data = {};
model.data.items = [];
model.success = false;
if (reportRefs && reportRefs.length) {
    var formAtachments = [];
    var formConnections = [];
    var routeDateString = "";
    var errandExecutor = document.assocs["lecm-errands:executor-assoc"][0];
    var executionAttachments = document.assocs["lecm-errands:execution-report-attachment-assoc"];
    var documentConnectionsAssoc = document.assocs["lecm-errands:execution-connected-document-assoc"];
    var executionReportText = document.properties["lecm-errands:execution-report"];
    var executionReportStatus = document.properties["lecm-errands:execution-report-status"];
    var formText = executionReportStatus != "PROJECT" ? "" : executionReportText;
    if (documentConnectionsAssoc) {
        formConnections.concat(documentConnectionsAssoc);
    }
    if(executionAttachments){
        formAtachments.concat(executionAttachments);
    }
    for (var i = 0; i < reportRefs.length; i++) {
        var report = search.findNode(reportRefs[i]);
        if (report && report.properties["lecm-errands-ts:coexecutor-report-status"] == "ACCEPT") {
            var document = documentTables.getDocumentByTableDataRow(report);
            var currentEmployee = orgstructure.getCurrentEmployee();
            if (document && lecmPermission.hasEmployeeDynamicRole(document, currentEmployee, "ERRANDS_EXECUTOR")) {
                var errandExecutorName = errandExecutor.properties["lecm-orgstr:employee-short-name"];
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
                var reportText = "<p>Отчет Соисполнителя " + reportCoexecutorName + ", направлен " + routeDateString + ":</p> ";
                if (reportRefs.length == 1 && executionReportStatus != "PROJECT"){
                    reportText = "<p>Использован отчет Соисполнителя " + reportCoexecutorName + ", направлен " + routeDateString + ":</p> ";
                }
                reportText += "<p>" + report.properties["lecm-errands-ts:coexecutor-report-text"] + "</p>";
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
                        if(!attachmentExist) {
                            formAtachments.push(reportAttachments[j]);
                            attachments.push({
                                name: reportAttachments[j].name,
                                link: "/share/page/document-attachment?nodeRef=" + reportAttachments[j].nodeRef
                            });
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
                        if (!documentConnectionsAssoc || !documentConnectionsAssoc.length || !assocExist) {
                            //document.createAssociation(reportConnections[k], "lecm-errands:execution-connected-document-assoc");
                            formConnections.push(reportConnections[k]);
                            connections.push({
                                name: reportConnections[k].name,
                                link: "/share/page/" + documentScript.getViewUrl(reportConnections[k]) + "?nodeRef=" + reportConnections[k].nodeRef
                            });
                        }
                        if (!hasAccess) {
                            lecmPermission.popAuthentication();
                        }
                    }
                }
                report.properties["lecm-errands-ts:coexecutor-report-is-transferred"] = true;
                document.save();
                report.save();
                var reportData = {
                    reportText: reportText,
                    attachments: attachments,
                    connections: connections
                };
                model.data.items.push(reportData);

            }
        }
    }
    model.data.formText = formText;
    model.data.formAtachments = formAtachments;
    model.data.formConnections = formConnections;
}
if (model.data.items.length) {
    model.success = true;
}