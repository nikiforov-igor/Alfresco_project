var ErrandCancelScript = {
    processCancelErrand: function (cancelChildren, reason) {
        if (cancelChildren) {
            lecmPermission.pushAuthentication();
            lecmPermission.setRunAsUserSystem();
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
            lecmPermission.popAuthentication();
        }
        var periodically = document.properties["lecm-errands:periodically"];
        if (!periodically) {
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
        }

        var msg = Packages.ru.it.lecm.base.beans.BaseBean.getMessage;
        var logText = msg('ru.it.lecm.errands.bjMessages.cancelErrand.message', "#initiator {cancel} ??????????????????: #mainobject.");
        logText = logText.replace("{cancel}", documentScript.wrapperTitle(msg('ru.it.lecm.errands.bjMessages.cancelErrand.cancelParamText', "??????????????"), reason));
        businessJournal.log(document.nodeRef.toString(), "CANCEL_ERRAND", logText, []);

        //ALFSED-2812 ?????????????????? ?????????????????? ?????????????? ???????????????? ???? ?????????????????????? ?????????? (?????????????? ???????? ???????????????? ???????????????? ?????????????? ????????????????????)
        statemachine.terminateWorkflowsByDefinition(document, 'errandsRequestDueDateChange_1', null, null);
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
                lecmPermission.pushAuthentication();
                lecmPermission.setRunAsUserSystem();
                var childrenErrands = errands.getChildErrands(document.nodeRef.toString());
                var childrenResolutions = errands.getChildResolutions(document.nodeRef.toString());
                childrenErrands.forEach(function (childErrand) {
                    if (!statemachine.isFinal(childErrand.nodeRef.toString()) && !statemachine.isDraft(childErrand)) {
                        //alfsed-732 ???????? ???????????????? ?????????????????????? ?????????????????????? ??????????????????
                        documentMembers.addMemberWithoutCheckPermission(childErrand, signalSender, true);

                        errands.sendCancelSignal(childErrand.nodeRef.toString(), reason, signalSender.nodeRef.toString());
                    }
                });
                childrenResolutions.forEach(function (childResolution) {
                    if (!statemachine.isFinal(childResolution.nodeRef.toString()) && !statemachine.isDraft(childResolution)) {
                        //alfsed-732 ???????? ???????????????? ?????????????????????? ?????????????????????? ??????????????????
                        documentMembers.addMemberWithoutCheckPermission(childResolution, signalSender, true);

                        resolutionsScript.sendAnnulSignal(childResolution.nodeRef.toString(), reason);
                    }
                });
                lecmPermission.popAuthentication();
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
            var msg = Packages.ru.it.lecm.base.beans.BaseBean.getMessage;
            var logText = msg('ru.it.lecm.errands.bjMessages.signalCancelErrand.message', "#object1 {cancel} ??????????????????: #mainobject.");
            logText = logText.replace("{cancel}", documentScript.wrapperTitle(msg('ru.it.lecm.errands.bjMessages.signalCancelErrand.cancelParamText', "??????????????"), reason));
            businessJournal.log(document.nodeRef.toString(), "CANCEL_ERRAND", logText, logObjects);
        }
    }
};