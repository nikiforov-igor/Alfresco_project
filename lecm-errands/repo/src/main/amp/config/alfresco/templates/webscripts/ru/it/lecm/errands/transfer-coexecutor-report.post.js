var reportRefs = [];
var requestContent = eval("(" + requestbody.getContent() + ")");
if (requestContent instanceof Array) {
    reportRefs = requestContent;
}
model.data = [];
model.success = false;
if (reportRefs && reportRefs.length) {
    for (var i = 0; i < reportRefs.length; i++) {
        var report = search.findNode(reportRefs[i]);
        if (report != null && report.properties["lecm-errands-ts:coexecutor-report-status"] == "ACCEPT" && !report.properties["lecm-errands-ts:coexecutor-report-is-transferred"]) {
            var document = documentTables.getDocumentByTableDataRow(report);
            var currentEmployee = orgstructure.getCurrentEmployee();
            if (document != null && lecmPermission.hasEmployeeDynamicRole(document, currentEmployee, "ERRANDS_EXECUTOR")) {
                var errandExecutor = document.assocs["lecm-errands:executor-assoc"][0];
                var errandExecutorName = errandExecutor.properties["lecm-orgstr:employee-short-name"];
                var reportCoexecutor = report.assocs["lecm-errands-ts:coexecutor-assoc"][0];
                var reportCoexecutorName = reportCoexecutor.properties["lecm-orgstr:employee-short-name"];
                var reportRouteDate = report.properties["lecm-errands-ts:coexecutor-report-route-date"];
                var dateFormat = new Packages.java.text.SimpleDateFormat("dd.MM.yyyy HH-mm-ss");
                var routeDateString = dateFormat.format(reportRouteDate).toString();
                var reportText = "<p>" + errandExecutorName + " отчитался отчетом Соисполнителя " + reportCoexecutorName + "(направлен " + routeDateString + "):</p> ";
                reportText += "<p>" + report.properties["lecm-errands-ts:coexecutor-report-text"] + "</p>";
                var attachments = [];
                var reportAttachments = report.assocs["lecm-errands-ts:coexecutor-report-attachment-assoc"];
                var category = documentAttachments.getCategoryByName("Исполнение", document);
                if (category && reportAttachments && reportAttachments.length) {
                    for (var j = 0; j < reportAttachments.length; j++) {
                        reportAttachments[j].move(category);
                        attachments.push({
                            name: reportAttachments[j].name,
                            link: "/share/page/document-attachment?nodeRef=" + reportAttachments[j].nodeRef
                        });
                    }
                }
                var connections = [];
                var reportConnections = report.assocs["lecm-errands-ts:coexecutor-report-connected-document-assoc"];
                if (reportConnections && reportConnections.length) {
                    for (var k = 0; k < reportConnections.length; k++) {
                         var assocExist = false;
                         var documentConnectionsAssoc = document.assocs["lecm-errands:execution-connected-document-assoc"];
                         if(documentConnectionsAssoc && documentConnectionsAssoc.length){
                            for(var l = 0; l < documentConnectionsAssoc.length; l++){
                                if(documentConnectionsAssoc[l].equals(reportConnections[k])){
                                    assocExist = true;
                                }
                            }
                         }
                         if(!documentConnectionsAssoc || !documentConnectionsAssoc.length || !assocExist) {
                            document.createAssociation(reportConnections[k],"lecm-errands:execution-connected-document-assoc");
                         }
                        connections.push({
                            name: reportConnections[k].name,
                            link: "/share/page/" + documentScript.getViewUrl(reportConnections[k]) + "?nodeRef=" + reportConnections[k].nodeRef
                        });
                    }
                }

                document.properties["lecm-errands:execution-report"] += reportText;
                report.properties["lecm-errands-ts:coexecutor-report-is-transferred"] = true;
                document.save();
                report.save();
                var reportData = {
                    reportText: reportText,
                    attachments: attachments,
                    connections: connections
                };
                model.data.push(reportData);

            }
        }
    }
}
if (model.data.length) {
    model.success = true;
}