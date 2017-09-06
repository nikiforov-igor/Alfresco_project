var AutoApprovalScript = {
    doAuto: function (doc, newApprovalState, logMessage, notificationCode) {
        var currentIteration = approvalRoutes.getDocumentCurrentIteration(doc);
        if (currentIteration && currentIteration.properties['lecm-routes-v2:status'] == "ACTIVE") {
            currentIteration.properties['lecm-routes-v2:status'] = newApprovalState;
            currentIteration.save();

            var stages = currentIteration.getChildAssocsByType('lecm-approval-route:stage');

            var stage, i, j, size, length, approvalState, items, item, itemState, isApproved, approver, approvers = [];

            for (i = 0, size = stages.length; i < size; ++i) {
                stage = stages[i];
                approvalState = '' + stage.properties['lecm-routes-v2:status'];

                items = stage.getChildAssocsByType('lecm-approval-route:stageItem');
                isApproved = false;

                for (j = 0, length = items.length; j < length; ++j) {
                    item = items[j];
                    itemState = '' + item.properties['lecm-routes-v2:status'];

                    approver = item.assocs["lecm-routes-v2:stageItemMemberAssoc"] && item.assocs["lecm-routes-v2:stageItemMemberAssoc"].length ?
                        item.assocs["lecm-routes-v2:stageItemMemberAssoc"][0] : null;
                    if (approver) {
                        approvers.push();
                    }

                    if ('NEW' == itemState || 'ACTIVE' == itemState) {
                        item.properties['lecm-routes-v2:status'] = 'CANCELLED';
                        item.properties['lecm-routes-v2:stageItemCompleteDate'] = base.getValueConverter().convertValueForJava(new Date());
                        item.save();
                    } else if ('APPROVED_EARLY' != itemState) {
                        isApproved = true;
                    }
                }

                if ("NEW" == approvalState) {
                    stage.properties['lecm-routes-v2:status'] = 'CANCELLED';
                } else {
                    stage.properties['lecm-routes-v2:status'] = isApproved ? 'FINISHED' : 'CANCELLED';
                }
                stage.save();
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

            edsDocument.sendChildChangeSignal(doc);
        }
    }
};