
var ErrandsCompletionSignalScript = {
    processCompletionSignal: function() {
        var doc = bpm_package.children[0];
        if (doc) {
            var hasAccess = lecmPermission.hasPermission(doc, "_WriteProperties");
            if (!hasAccess) {
                lecmPermission.pushAuthentication();
                lecmPermission.setRunAsUserSystem();
            }
            var signalSender = null;
            var signalSenderAssoc = doc.assocs["lecm-eds-aspect:completion-signal-sender-assoc"];
            if (signalSenderAssoc && signalSenderAssoc.length) {
                signalSender = signalSenderAssoc[0];
            }
            var signalReason = doc.properties["lecm-eds-aspect:completion-signal-reason"];
            var closeChild = doc.properties["lecm-eds-aspect:completion-signal-close-child"];
            if (closeChild) {
                var childrenErrands = errands.getChildErrands(doc.nodeRef.toString());
                var childrenResolutions = errands.getChildResolutions(doc.nodeRef.toString());
                childrenErrands.forEach(function (childErrand) {
                    if (!statemachine.isFinal(childErrand.nodeRef.toString()) && !statemachine.isDraft(childErrand)) {
                        //alfsed-732 фикс закрытия недоступных контроллеру поручений
                        documentMembers.addMemberWithoutCheckPermission(childErrand, signalSender, "LECM_BASIC_PG_Reader", true);

                        edsDocument.sendCompletionSignal(childErrand, signalReason, signalSender);
                    }
                });
                childrenResolutions.forEach(function (childResolution) {
                    if (!statemachine.isFinal(childResolution.nodeRef.toString()) && !statemachine.isDraft(childResolution)) {
                        //alfsed-732 фикс закрытия недоступных контроллеру поручений
                        documentMembers.addMemberWithoutCheckPermission(childResolution, signalSender, "LECM_BASIC_PG_Reader", true);

                        edsDocument.sendCompletionSignal(childResolution, signalReason, signalSender);
                    }
                });
            }
            if (signalReason) {
                if (doc.properties["lecm-errands:execution-report-status"] == "PROJECT") {
                    doc.properties["lecm-errands:execution-report"] += '<p>' + signalReason + '</p>';
                } else if (!doc.properties["lecm-errands:execution-report-status"]) {
                    doc.properties["lecm-errands:execution-report"] = signalReason;
                }
            } else {
                var reportText = "Завершено по инициативе ";
                reportText += documentScript.wrapperLink(signalSender, signalSender.properties["lecm-orgstr:employee-short-name"]);
                if (doc.properties["lecm-errands:execution-report-status"] == "PROJECT") {
                    doc.properties["lecm-errands:execution-report"] += '<p>' + reportText + '</p>';
                } else if (!doc.properties["lecm-errands:execution-report-status"]) {
                    doc.properties["lecm-errands:execution-report"] = reportText;
                }
            }
            doc.properties["lecm-errands:execution-report-status"] = "ACCEPT";
            doc.save();
            if (!hasAccess) {
                lecmPermission.popAuthentication();
            }
            var recipients = [];
            var authorAssoc = doc.assocs["lecm-errands:initiator-assoc"];
            if (authorAssoc && authorAssoc.length) {
                recipients.push(authorAssoc[0]);
            }
            var executorAssoc = doc.assocs["lecm-errands:executor-assoc"];
            if (executorAssoc && executorAssoc.length) {
                recipients.push(executorAssoc[0]);
            }

            var soExecutors = doc.assocs["lecm-errands:coexecutors-assoc"];
            if (soExecutors) {
                soExecutors.forEach(function (coexecutor) {
                    recipients.push(coexecutor);
                });
            }

            var controllerAssoc = doc.assocs["lecm-errands:controller-assoc"];
            if (controllerAssoc && controllerAssoc.length == 1) {
                recipients.push(controllerAssoc[0]);
            }
            notifications.sendNotificationFromCurrentUser({
                recipients: recipients,
                templateCode: 'ERRANDS_EXECUTED_WITHOUT_REPORT',
                templateConfig: {
                    mainObject: doc,
                    eventExecutor: signalSender
                },
                dontCheckAccessToObject: true
            });
            var logObjects = [];
            logObjects.push("" + signalSender.nodeRef);
            var logText = "#object1 ";
            logText += documentScript.wrapperTitle("завершил", signalReason ? signalReason : "");
            logText += "исполнение поручения #mainobject.";
            businessJournal.log(doc.nodeRef.toString(), "EXECUTE_DOCUMENT", logText, logObjects);

            edsDocument.resetCompletionSignal(doc);
        }
    }
};
