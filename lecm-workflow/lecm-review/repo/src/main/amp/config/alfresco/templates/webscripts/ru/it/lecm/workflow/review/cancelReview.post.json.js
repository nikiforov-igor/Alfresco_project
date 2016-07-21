/* global utils, notifications, json, model */

(function () {
	var document = utils.getNodeFromString(json.get('documentRef')),
		reviewItem = utils.getNodeFromString(json.get('nodeRef')),
		currentEmployee = orgstructure.getCurrentEmployee(),
		recipients = [];

		recipients.push(reviewItem.assocs['lecm-review-ts:reviewer-assoc'][0]);
		reviewItem.properties['lecm-review-ts:review-state'] = 'CANCELLED';
		reviewItem.properties['lecm-review-ts:review-finish-date'] = new Date();
		reviewItem.save();

	if (recipients && recipients.length) {
		notifications.sendNotificationFromCurrentUser({
			recipients: recipients,
			templateCode: 'REVIEW_CANCELED',
			templateConfig: {
				mainObject: document,
				initiator: currentEmployee
			}
		});
	}

	model.nodeRef = json.get('nodeRef');

})();
