(function () {
	var document = utils.getNodeFromString(json.get('documentRef')),
		reviewInfo = utils.getNodeFromString(json.get('nodeRef')),
		items = reviewInfo.sourceAssocs['lecm-review-info:info-assoc'],
		i, recipients = [];

	if (items && items.length) {
		for (i in items) {
			recipients.push(items[i].assocs['lecm-review-ts:reviewer-assoc'][0]);
			items[i].properties['lecm-review-ts:review-state'] = 'CANCELLED';
			items[i].properties['lecm-review-ts:review-finish-date'] = new Date();
			items[i].save();
		}
		reviewInfo.properties['lecm-review-info:review-state'] = 'CANCELLED';
		reviewInfo.save();
	}

	if (recipients && recipients.length) {
		notifications.sendNotificationFromCurrentUser({
			recipients: recipients,
			templateCode: 'REVIEW_CANCELED',
			templateConfig: {
				mainObject: document
			}
		});
	}

})();
