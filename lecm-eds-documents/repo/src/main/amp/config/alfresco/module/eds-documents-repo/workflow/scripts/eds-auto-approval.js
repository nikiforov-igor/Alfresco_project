var AutoApprovalScript = {
    doAuto: function (doc, newApprovalState, logMessage, notificationCode) {
        var currentIteration = approvalRoutes.getDocumentCurrentIteration(doc);
        if (currentIteration && currentIteration.properties['lecm-routes-v2:status'] == "ACTIVE") {
            var stages = currentIteration.getChildAssocsByType('lecm-approval-route:stage');
            var stage, i, j, size, length, items, item, approver, approvers = [];

            for (i = 0, size = stages.length; i < size; ++i) {
                stage = stages[i];
                items = stage.getChildAssocsByType('lecm-approval-route:stageItem');
                for (j = 0, length = items.length; j < length; ++j) {
                    item = items[j];
                    approver = item.assocs["lecm-routes-v2:stageItemMemberAssoc"] && item.assocs["lecm-routes-v2:stageItemMemberAssoc"].length ?
                        item.assocs["lecm-routes-v2:stageItemMemberAssoc"][0] : null;
                    if (approver) {
                        approvers.push(approver);
                    }
                }
            }

            businessJournal.log(doc.nodeRef.toString(), 'APPROVAL', substitude.formatNodeTitle(doc.nodeRef.toString(), logMessage.toString()), []);

            approvers.push(currentIteration.assocs["lecm-routes-v2:initiatorEmployeeAssoc"][0]);

            notifications.sendNotification({
                recipients: approvers,
                templateCode: notificationCode,
                templateConfig: {
                    mainObject: doc
                }
            });

            statemachine.terminateWorkflowsByDefinition(doc, 'lecmApprovementWorkflow-v2', 'forcedDecision', newApprovalState);
        }
    }
};