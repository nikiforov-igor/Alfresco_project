var ErrandCancelScript = {
    processCancelErrand: function (cancelChildren, reason) {
        if (cancelChildren) {
            var childrenErrands = errands.getChildErrands(document.nodeRef.toString());
            var childrenResolutions = errands.getChildResolutions(document.nodeRef.toString());
            childrenErrands.forEach(function (childErrand) {
                if (!statemachine.isFinal(childErrand.nodeRef.toString()) && !statemachine.isDraft(childErrand)) {
                    errands.sendCancelSignal(childErrand.nodeRef.toString(), reason, currentUser.nodeRef.toString());
                }
            });
            childrenResolutions.forEach(function (childResolution) {
                if (!statemachine.isFinal(childResolution.nodeRef.toString()) && !statemachine.isDraft(childResolution)) {
                    resolutionsScript.sendAnnulSignal(childResolution.nodeRef.toString(), reason);
                }
            });
        }
        var soExecutors = document.assocs["lecm-errands:coexecutors-assoc"];
        if (soExecutors) {
            soExecutors.forEach(function (coexecutor) {
                recipients.push(coexecutor);
            });
        }

        var controllerAssoc = document.assocs["lecm-errands:controller-assoc"];
        if (controllerAssoc && controllerAssoc.length == 1) {
            recipients.push(controllerAssoc[0]);
        }

        notifications.sendNotificationFromCurrentUser({
            recipients: recipients,
            templateCode: 'ERRANDS_CANCEL',
            templateConfig: {
                mainObject: document,
                eventExecutor: currentUser,
                comment: reason
            },
            dontCheckAccessToObject: true
        });

        var logText = "#initiator ";
        logText += documentScript.wrapperTitle("отменил", reason);
        logText += " поручение: #mainobject.";
        businessJournal.log(document.nodeRef.toString(), "CANCEL_ERRAND", logText, []);
    },

    processCancelSignal: function () {
        var document = bpm_package.children[0];
        if (document) {
            var recipients = [];
            var reason = "";
            if (document.properties["lecm-errands:cancellation-signal-reason"]) {
                reason = document.properties["lecm-errands:cancellation-signal-reason"];
            }
            var signalSender = document.assocs["lecm-errands:cancellation-signal-sender-assoc"][0];
            if (document.properties["lecm-errands:cancel-children"]) {
                var childrenErrands = errands.getChildErrands(document.nodeRef.toString());
                var childrenResolutions = errands.getChildResolutions(document.nodeRef.toString());
                childrenErrands.forEach(function (childErrand) {
                    if (!statemachine.isFinal(childErrand.nodeRef.toString()) && !statemachine.isDraft(childErrand)) {
                        errands.sendCancelSignal(childErrand.nodeRef.toString(), reason, signalSender.nodeRef.toString());
                    }
                });
                childrenResolutions.forEach(function (childResolution) {
                    if (!statemachine.isFinal(childResolution.nodeRef.toString()) && !statemachine.isDraft(childResolution)) {
                        resolutionsScript.sendAnnulSignal(childResolution.nodeRef.toString(), reason);
                    }
                });
            }

            var executorAssoc = document.assocs["lecm-errands:executor-assoc"];
            if (executorAssoc && executorAssoc.length) {
                recipients.push(executorAssoc[0]);
            }

            var soExecutors = document.assocs["lecm-errands:coexecutors-assoc"];
            if (soExecutors) {
                soExecutors.forEach(function (coexecutor) {
                    recipients.push(coexecutor);
                });
            }

            var controllerAssoc = document.assocs["lecm-errands:controller-assoc"];
            if (controllerAssoc && controllerAssoc.length == 1) {
                recipients.push(controllerAssoc[0]);
            }

            notifications.sendNotificationFromCurrentUser({
                recipients: recipients,
                templateCode: 'ERRANDS_CANCEL',
                templateConfig: {
                    mainObject: document,
                    eventExecutor: signalSender,
                    comment: reason
                },
                dontCheckAccessToObject: true
            });
            var logObjects = [];
            logObjects.push("" + signalSender.nodeRef);
            var logText = "#object1 ";
            logText += documentScript.wrapperTitle("отменил", reason);
            logText += " поручение: #mainobject.";
            businessJournal.log(document.nodeRef.toString(), "CANCEL_ERRAND", logText, logObjects);
        }
    }
};