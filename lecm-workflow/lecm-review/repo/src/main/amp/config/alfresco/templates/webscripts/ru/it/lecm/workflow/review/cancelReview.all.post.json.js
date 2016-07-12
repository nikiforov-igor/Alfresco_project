/* global utils, orgstructure, notifications, model, json */

(function () {
	var document = utils.getNodeFromString(json.get('documentRef')),
		reviewTable = document.assocs['lecm-review-ts:review-table-assoc'][0],
		reviewTsItems = reviewTable.getChildAssocsByType('lecm-review-ts:review-table-item'),
		currentEmployee = orgstructure.getCurrentEmployee(),
		recipients = [],
		i, item, initiators, initiator, state;

	for (i in reviewTsItems) {
		item = reviewTsItems[i];
		state = '' + item.properties['lecm-review-ts:review-state'];
		initiators = item.assocs['lecm-review-ts:initiator-assoc'];
		if (initiators && initiators.length) {
			initiator = initiators[0];
			if (initiator.equals(currentEmployee) && 'NOT_REVIEWED' == state) {
				recipients.push(item.assocs['lecm-review-ts:reviewer-assoc'][0]);
				item.properties['lecm-review-ts:review-state'] = 'CANCELLED';
				item.properties['lecm-review-ts:review-finish-date'] = new Date();
				item.save();
			}
		}
	}
	if (recipients && recipients.length) {
		notifications.sendNotificationFromCurrentUser({
			recipients: recipients,
			templateCode: 'REVIEW_CANCELED_ALL',
			templateConfig: {
				mainObject: document
			}
		});
	}

	model.nodeRef = reviewTable.nodeRef.toString();
	model.canceled = recipients.length;
})();