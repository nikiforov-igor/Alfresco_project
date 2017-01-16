function processChangeExecutor(newExecutor, reason) {
    document.removeAssociation(executor, "lecm-errands:executor-assoc");
    document.createAssociation(newExecutor, "lecm-errands:executor-assoc");
    statemachine.grandDynamicRoleForEmployee(document, newExecutor, "ERRANDS_EXECUTOR");
    documentMembers.addMember(document, executor, "ERRANDS_READER");
    lecmPermission.revokeDynamicRole(document, executor, "ERRANDS_EXECUTOR");

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
            reason: ""
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