var ExecuteErrandScript = {

    processChildExecutedSignal: function (doc) {
        var children = [];
        var childrenErrands = errands.getChildErrands(doc.nodeRef.toString());
        var childrenResolutions = errands.getChildResolutions(doc.nodeRef.toString());
        if (childrenErrands) {
            children = children.concat(childrenErrands);
        }
        if (childrenResolutions) {
            children = children.concat(childrenResolutions);
        }
        var completionReason = doc.properties["lecm-eds-aspect:completion-signal-reason"];
        var isProcessExecutedChild = children.some(function (child) {
            var isStatusOk = child.properties["lecm-statemachine:status"] == "Исполнено";
            var isAutoClose = child.properties["lecm-errands:auto-close"];
            var isCompleteReasonOk = child.properties["lecm-errands:execution-report"] == completionReason;
            return isStatusOk && isAutoClose && isCompleteReasonOk;
        });
        if (isProcessExecutedChild) {
            if (doc.properties["lecm-errands:execution-report-status"] == "PROJECT") {
                doc.properties["lecm-errands:execution-report"] += '<p>' + doc.properties["lecm-eds-aspect:completion-signal-reason"] + '</p>';
            } else if (!doc.properties["lecm-errands:execution-report-status"]) {
                doc.properties["lecm-errands:execution-report-status"] = "PROJECT";
                doc.properties["lecm-errands:execution-report"] = doc.properties["lecm-eds-aspect:completion-signal-reason"];
            }
            ExecuteErrandScript.executeErrand(doc, false);
            doc.save();
        }
    },
    processReviewErrandChildExecuted: function(document){
        var childrenErrands = errands.getChildErrands(doc.nodeRef.toString());
        var childrenResolutions = errands.getChildResolutions(doc.nodeRef.toString());
        var childErrandExecuted = false;
        var allChildCompleted = true;
        var i;
        for (i = 0; i < childrenErrands.length; i++) {
            if (!statemachine.isDraft(childrenErrands[i]) && statemachine.isFinal(childrenErrands[i].nodeRef.toString())) {
                if (childrenErrands[i].properties["lecm-statemachine:status"] == "Исполнено") {
                    childErrandExecuted = true;
                }
            } else {
                allChildCompleted = false;
                break;
            }
        }
        var childResolutionCompleted = false;
        for (i = 0; i < childrenResolutions.length; i++) {
            if (!statemachine.isDraft(childrenResolutions[i]) && statemachine.isFinal(childrenResolutions[i].nodeRef.toString())) {
                if (childrenResolutions[i].properties["lecm-statemachine:status"] == "Завершено") {
                    childResolutionCompleted = true;
                }
            } else {
                allChildCompleted = false;
                break;
            }
        }
        if (allChildCompleted && (childErrandExecuted || childResolutionCompleted)) {
            document.properties["lecm-errands:execution-report"] = "Исполнение завершено";
            document.properties["lecm-errands:execution-report-status"] = "ACCEPT";
            document.properties["lecm-errands:transit-to-executed"] = true;
            document.save();
        }
    },

    executeErrand: function (document, closeChild) {
        var reportRequired = document.properties["lecm-errands:report-required"];
        var currentUser = orgstructure.getCurrentEmployee();
        var recipients = [];
        var notificationTemplate = null;
        var author = null;
        var authorAssoc = document.assocs["lecm-errands:initiator-assoc"];
        if (authorAssoc && authorAssoc.length == 1) {
            author = authorAssoc[0];
        }
        var controller = null;
        var controllerAssoc = document.assocs["lecm-errands:controller-assoc"];
        if (controllerAssoc && controllerAssoc.length == 1) {
            controller = controllerAssoc[0];
        }
        if (closeChild) {
            var reason = "Завершено исполнением поручения-основания ";
            reason += document.properties["lecm-document:present-string"];
            var childrenErrands = errands.getChildErrands(document.nodeRef.toString());
            var childrenResolutions = errands.getChildResolutions(document.nodeRef.toString());
            childrenErrands.forEach(function (childErrand) {
                if (!statemachine.isFinal(childErrand.nodeRef.toString()) && !statemachine.isDraft(childErrand)) {
                    edsDocument.sendCompletionSignal(childErrand, reason, currentUser);
                }
            });
            childrenResolutions.forEach(function (childResolution) {
                if (!statemachine.isFinal(childResolution.nodeRef.toString()) && !statemachine.isDraft(childResolution)) {
                    edsDocument.sendCompletionSignal(childResolution, reason, currentUser);
                }
            });
        }
        if (!reportRequired) {
            document.properties["lecm-errands:execution-report-status"] = "ACCEPT";
            document.properties["lecm-errands:execute-result"] = "executed";
            notificationTemplate = "ERRANDS_EXECUTED_WITHOUT_REPORT";
            if (author) {
                recipients.push(author);
            }
            if (controller) {
                recipients.push(controller);
            }
            var additionalDoc = errands.getAdditionalDocument(document.nodeRef.toString());
            if (additionalDoc) {
                lecmPermission.pushAuthentication();
                lecmPermission.setRunAsUserSystem();
                additionalDoc.properties["lecm-eds-aspect:completion-signal-reason"] = document.properties["lecm-errands:execution-report"];
                additionalDoc.save();
                lecmPermission.popAuthentication();
            }
        } else {
            document.properties["lecm-errands:execution-report-status"] = "ONCONTROL";
            var reportRecipientType = document.properties["lecm-errands:report-recipient-type"];
            notificationTemplate = "ERRANDS_EXECUTED_WITH_REPORT";
            if (reportRecipientType == "AUTHOR" && author) {
                recipients = [author];
            } else if (reportRecipientType == "CONTROLLER" && controller) {
                recipients = [controller];
            } else if (reportRecipientType == "AUTHOR_AND_CONTROLLER") {
                if (author) {
                    recipients.push(author);
                }
                if (controller) {
                    recipients.push(controller);
                }
            }
            document.properties["lecm-errands:execute-result"] = "onControl";
        }
        notifications.sendNotificationFromCurrentUser({
            recipients: recipients,
            templateCode: notificationTemplate,
            templateConfig: {
                mainObject: document,
                eventExecutor: currentUser
            }
        });
        document.properties["lecm-errands:project-report-text"] = null;
        document.properties["lecm-errands:project-report-attachment"] = null;
        document.properties["lecm-errands:project-report-connections"] = null;
        document.save();
    },
    fillExecutionReport: function (document, attachments, connectedDocuments, reportText, closeChild) {
        var reportAttachments = document.assocs["lecm-errands:execution-report-attachment-assoc"];
        var totalNewAttachments = [];
        var totalRemovedAttachments = [];

        if (attachments) {
            if (reportAttachments && reportAttachments.length) {
                totalNewAttachments = attachments.filter(function(attachment) {
                    for (var i = 0; i < reportAttachments.length; i++) {
                        if (attachment.nodeRef.equals(reportAttachments[i].nodeRef)) {
                            return false;
                        }
                    }
                    return true;
                });
                totalRemovedAttachments = reportAttachments.filter(function(attachment) {
                    for (var i = 0; i < attachments.length; i++) {
                        if (attachment.nodeRef.equals(attachments[i].nodeRef)) {
                            return false;
                        }
                    }
                    return true;
                });
            } else {
                totalNewAttachments = attachments;
            }
        } else {
            if (reportAttachments && reportAttachments.length) {
                totalRemovedAttachments = reportAttachments;
            }
        }
        totalNewAttachments.forEach(function(attachment) {
            document.createAssociation(attachment, "lecm-errands:execution-report-attachment-assoc");
        });
        totalRemovedAttachments.forEach(function(attachment) {
            document.removeAssociation(attachment, "lecm-errands:execution-report-attachment-assoc");
        });

        var reportConnectedDocs = document.assocs["lecm-errands:execution-connected-document-assoc"];
        var totalNewConnectedDocs = [];
        var totalRemovedConnectedDocs = [];
        if (connectedDocuments) {
            if (reportConnectedDocs && reportConnectedDocs.length) {
                totalNewConnectedDocs = connectedDocuments.filter(function(doc) {
                    for (var i = 0; i < reportConnectedDocs.length; i++) {
                        if (doc.nodeRef.equals(reportConnectedDocs[i].nodeRef)) {
                            return false;
                        }
                    }
                    return true;
                });
                totalRemovedConnectedDocs = reportConnectedDocs.filter(function(doc) {
                    for (var i = 0; i < connectedDocuments.length; i++) {
                        if (doc.nodeRef.equals(connectedDocuments[i].nodeRef)) {
                            return false;
                        }
                    }
                    return true;
                });
            } else {
                totalNewConnectedDocs = connectedDocuments;
            }
        } else {
            if (reportConnectedDocs && reportConnectedDocs.length) {
                totalRemovedConnectedDocs = reportConnectedDocs;
            }
        }
        totalNewConnectedDocs.forEach(function(doc) {
            if (doc) {
                document.createAssociation(doc, "lecm-errands:execution-connected-document-assoc");
            }
        });
        totalRemovedConnectedDocs.forEach(function(doc) {
            if (doc) {
                document.removeAssociation(doc, "lecm-errands:execution-connected-document-assoc");
            }
        });
        document.properties["lecm-errands:execution-report-close-child"] = closeChild;
        document.properties["lecm-errands:execution-report"] = reportText;
        document.save();
    }
};