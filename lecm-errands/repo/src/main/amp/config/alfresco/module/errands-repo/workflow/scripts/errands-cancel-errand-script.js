function processCancelErrand(cancelChildren, reason) {
    if (cancelChildren) {
        errands.sendCancelChildrenSignal(document.nodeRef.toString(), reason);
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
}