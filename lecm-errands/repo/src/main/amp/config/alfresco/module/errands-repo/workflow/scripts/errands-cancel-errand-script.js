function processCancelErrand(cancelChildren, reason) {
    if (cancelChildren) {
        var childrenErrands = errands.getChildErrands(document.nodeRef.toString());
        var childrenResolutions = errands.getChildResolutions(document.nodeRef.toString());
        childrenErrands.forEach(function (childErrand) {
            if (!statemachine.isFinal(childErrand.nodeRef.toString()) && !statemachine.isDraft(childErrand)) {
                errands.sendCancelSignal(childErrand.nodeRef.toString(), reason);
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
}