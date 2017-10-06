function processChangeExecutor(newExecutor, reason) {
    document.removeAssociation(executor, "lecm-errands:executor-assoc");
    document.createAssociation(newExecutor, "lecm-errands:executor-assoc");
    statemachine.grandDynamicRoleForEmployee(document, newExecutor, "ERRANDS_EXECUTOR");
    lecmPermission.revokeDynamicRole(document, executor, "ERRANDS_EXECUTOR");
    documentMembers.addMember(document, executor, "ERRANDS_READER", true);
    documentMembers.addMember(document, newExecutor, "ERRANDS_READER", true);

    if (errands.isTransferRightToBaseDocument()) {
        var baseDocumentAssoc = document.assocs["lecm-errands:base-assoc"];
        if (baseDocumentAssoc && baseDocumentAssoc.length) {
            documentMembers.addMemberWithoutCheckPermission(baseDocumentAssoc[0], newExecutor, "LECM_BASIC_PG_Reader", true);
        }
    }

    notifications.sendNotificationFromCurrentUser({
        recipients: [newExecutor],
        templateCode: 'ERRANDS_CHANGE_EXECUTOR_NEW',
        templateConfig: {
            mainObject: document,
            eventExecutor: currentUser
        }
    });
    notifications.sendNotificationFromCurrentUser({
        recipients: [executor],
        templateCode: 'ERRANDS_CHANGE_EXECUTOR_OLD',
        templateConfig: {
            mainObject: document,
            eventExecutor: currentUser,
            reason: reason
        }
    });
    var recipients = [];
    var controllerAssoc = document.assocs["lecm-errands:controller-assoc"];
    var coexecutorsAssoc = document.assocs["lecm-errands:coexecutors-assoc"];
    if (controllerAssoc && controllerAssoc.length == 1) {
        recipients.push(controllerAssoc[0]);
    }
    if (coexecutorsAssoc && coexecutorsAssoc.length) {
        for (var i = 0; i < coexecutorsAssoc.length; i++) {
            recipients.push(coexecutorsAssoc[i]);
        }
    }
    notifications.sendNotificationFromCurrentUser({
        recipients: recipients,
        templateCode: 'ERRANDS_CHANGE_EXECUTOR',
        templateConfig: {
            mainObject: document,
            eventExecutor: currentUser,
            reason: reason
        }
    });
}