(function () {
    var report = search.findNode(args.nodeRef);
    var document = documentTables.getDocumentByTableDataRow(report);
    var currentUser = orgstructure.getCurrentEmployee();
    var attrs = eval('(' + requestbody.getContent() + ')');
    if (document && report) {
        var isRoute = attrs["prop_lecm-errands-ts_coexecutor-report-is-route"];
        var connectedDocumentRef = attrs['prop_lecm-errands-ts_connected-document-ref'];
        var connectedDocument = null;
        if (connectedDocumentRef) {
            connectedDocument = search.findNode(connectedDocumentRef);
        }
        var oldConnectedDocumentRef = report.properties['prop_lecm-errands-ts_connected-document-ref'];
        var oldConnectedDocumentLinkAssoc = report.assocs["lecm-errands-ts:coexecutor-report-connected-document-link-assoc"];
        var oldAttachment = report.assocs["lecm-errands-ts:coexecutor-report-attachment-assoc"];
        var attachmentRef = attrs["assoc_lecm-errands-ts_coexecutor-report-attachment-assoc"];
        var attachment = null;
        if (attachmentRef) {
            attachment = search.findNode(attachmentRef);
        }
        var reportText = attrs["prop_lecm-errands-ts_coexecutor-report-text"];

        //удаляем старые ассоциации если не равны новым
        if (connectedDocument) {
            //если документы отличаются
            if (!connectedDocumentRef.equals(oldConnectedDocumentRef)) {
                if (oldConnectedDocumentLinkAssoc && oldConnectedDocumentLinkAssoc.length) {
                    report.removeAssociation(oldConnectedDocumentLinkAssoc[0], "lecm-errands-ts:coexecutor-report-connected-document-link-assoc");
                    oldConnectedDocumentLinkAssoc[0].remove();
                }
                //если ссылка уже есть
                var links = errands.getLinks(document.nodeRef);
                var i, link;
                var linkExist = false;
                if (links && links.length) {
                    for (i = 0; i < links.length; i++) {
                        if (links[i].name.equals(connectedDocument.name)) {
                            linkExist = true;
                            break;
                        }
                    }
                }

                if (!linkExist) {
                    link = errands.createCoexecutorReportLink(document.nodeRef, connectedDocument.name, connectedDocumentRef);
                } else {
                    link = links[i];
                }
                report.createAssociation(link, "lecm-errands-ts:coexecutor-report-connected-document-link-assoc");
                report.properties["lecm-errands-ts:connected-document-ref"] = connectedDocumentRef;
            }
        } else {
            if (oldConnectedDocumentLinkAssoc && oldConnectedDocumentLinkAssoc.length) {
                report.removeAssociation(oldConnectedDocumentLinkAssoc[0], "lecm-errands-ts:coexecutor-report-connected-document-link-assoc");
                //если ссылка нигде не используется - удалаяем
                var allReports = documentTables.getTableDataRows(report.parent.nodeRef);
                var linkIsUsed = false;
                if (allReports && allReports.length) {
                    allReports.forEach(function (report) {
                        var linkAssoc = report.assocs["lecm-errands-ts:coexecutor-report-connected-document-link-assoc"];
                        if (linkAssoc && linkAssoc.length) {
                            if (linkAssoc[0].nodeRef.equals(oldConnectedDocumentLinkAssoc[0].nodeRef)) {
                                linkIsUsed = true;
                            }
                        }

                    });
                }
                if (!linkIsUsed) {
                    oldConnectedDocumentLinkAssoc[0].remove();
                }
            }
            report.properties["lecm-errands-ts:connected-document-ref"] = null;
        }
        if (attachment) {
            if ((oldAttachment && oldAttachment.length && !oldAttachment[0].nodeRef.equals(attachment.nodeRef)) || (!oldAttachment || !oldAttachment.length)) {
                var dateFormat = new Packages.java.text.SimpleDateFormat("dd.MM.yyyy HH-mm-ss");
                var dateString = dateFormat.format(new java.util.Date());
                attachment.properties["cm:name"] = "Отчет соисполнителя " + currentUser.properties["lecm-orgstr:employee-short-name"] + ", от " + dateString;
                attachment.save();

                var category = documentAttachments.getCategoryByName("Отчеты соисполнителей", document);
                if (category != null) {
                    attachment.move(category);
                }
                report.createAssociation(attachment, "lecm-errands-ts:coexecutor-report-attachment-assoc");

                if (oldAttachment && oldAttachment.length && !oldAttachment[0].nodeRef.equals(attachment.nodeRef)) {
                    report.removeAssociation(oldAttachment[0], "lecm-errands-ts:coexecutor-report-attachment-assoc");
                    documentAttachments.deleteAttachment(oldAttachment[0].nodeRef);
                }
            }

        } else {
            if (oldAttachment && oldAttachment.length) {
                report.removeAssociation(oldAttachment[0], "lecm-errands-ts:coexecutor-report-attachment-assoc");
                documentAttachments.deleteAttachment(oldAttachment[0].nodeRef);
            }
        }
        if (reportText) {
            report.properties["lecm-errands-ts:coexecutor-report-text"] = reportText;
        }

        if (isRoute == "true") {
            report.properties["lecm-errands-ts:coexecutor-report-status"] = "ONCONTROL";
            report.properties["lecm-errands-ts:coexecutor-report-route-date"] = new Date();

            if (connectedDocument) {
                //связываем если еще не связан
                var connectDocs = documentConnection.getConnectedDocuments(document, 'docReport', "lecm-document:base");
                var i, connectExist;
                for (i = 0; i < connectDocs.length; i++) {
                    if (connectDocs[i].nodeRef.equals(connectedDocument.nodeRef)) {
                        connectExist = true;
                    }
                }
                if (!connectExist) {
                    documentConnection.createConnection(document, connectedDocument, 'docReport', false);
                }
            }

            var recipients = [];
            var executorAssoc = document.assocs["lecm-errands:executor-assoc"];
            if (executorAssoc != null && executorAssoc.length > 0) {
                recipients.push(executorAssoc[0]);
            }

            notifications.sendNotificationFromCurrentUser({
                recipients: recipients,
                templateCode: 'ERRANDS_COEXEC_REPORT',
                templateConfig: {
                    mainObject: document,
                    eventExecutor: currentUser
                },
                dontCheckAccessToObject: true
            });
            var logObjects = [];
            var logText = "Соисполнитель #initiator создал";
            logText += "отчет о выполнении поручения #mainobject";
            businessJournal.log(document.nodeRef.toString(), "ERRAND_СOEXECUTOR_REPORT", logText, logObjects);
        }
        report.save();
        model.report = report;

    }

})();