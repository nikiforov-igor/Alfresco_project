/* global logger, search, orgstructure, documentMembers, notifications, businessJournal, review, documentTables */

function createReviewTSItem(persistedObject) {
	try {
		var reviewItem = search.findNode(persistedObject);
		var currentEmployee = orgstructure.getCurrentEmployee();
		if (reviewItem.typeShort == 'lecm-review-ts:review-table-item') {
			reviewItem.createAssociation(currentEmployee, 'lecm-review-ts:initiator-assoc');
//			var reviewObjects = reviewItem.assocs['lecm-review-ts:reviewer-assoc'];
//			if ((reviewObjects.length > 0) || (reviewObjects[0].typeShort == 'lecm-review-list:review-list-item')) {
			reviewItem.addAspect('sys:temporary');
			review.processItem(reviewItem);
//			}
		}
	} catch (error) {
		var msg = error.message;
		status.setCode(500, msg);

		if (logger.isLoggingEnabled()) {
			logger.log(msg);
			logger.log('Returning 500 status code');
		}
	}
}

function createReviewTSItem(reviewInfo, reviewTsItems) {
	// надо достать имплоев из ассоков и для каждого склепать итем
	var i, j, item,
		items = [],
		employee,
		employees = {},
		assocs,
		currentEmployee = orgstructure.getCurrentEmployee(),
		rootFolder = reviewInfo.parent;

	reviewInfo.createAssociation(currentEmployee, 'lecm-review-info:initiator-assoc');

	for (i in reviewTsItems) {
		item = reviewTsItems[i];
		if ('lecm-orgstr:employee' == item.typeShort) {
			employees[item.nodeRef.toString()] = true;
		}
		if ('lecm-review-list:review-list-item' == item.typeShort) {
			assocs = item.assocs['lecm-review-list:reviewer-assoc'];
			if (assocs && assocs.length) {
				for (j in assocs) {
					employees[assocs[j].nodeRef.toString()] = true;
				}
			}
		}
	}
	for (i in employees) {
		employee = utils.getNodeFromString(i);
		item = rootFolder.createNode(null, 'lecm-review-ts:review-table-item', {
			// 'lecm-document:indexTableRow': documentTables.getTableTotalRow(rootFolder.nodeRef.toString())
		});
		item.createAssociation(currentEmployee, 'lecm-review-ts:initiator-assoc');
		item.createAssociation(employee, 'lecm-review-ts:reviewer-assoc');
		item.createAssociation(reviewInfo, 'lecm-review-info:info-assoc');
		items.push(item);
	}
	return items;
}

function sendToReview(reviewInfo, items) {
	var document = documentTables.getDocumentByTableDataRow(reviewInfo),
		initiator = orgstructure.getCurrentEmployee(),
		recipients = [],
		row;

	for each (row in items) {
		var reviewer = row.associations['lecm-review-ts:reviewer-assoc'][0];
		var itemInitiator = row.assocs['lecm-review-ts:initiator-assoc'];
		if (itemInitiator && itemInitiator.length && initiator.nodeRef.equals(itemInitiator[0].nodeRef)) {
			if (row.properties['lecm-review-ts:review-state'] == 'NOT_STARTED') {
				documentMembers.addMemberWithoutCheckPermission(document, reviewer, 'LECM_BASIC_PG_Reader', true);
				recipients.push(reviewer);
				row.properties['lecm-review-ts:review-state'] = 'NOT_REVIEWED';
				row.properties['lecm-review-ts:review-start-date'] = new Date();
				row.save();
			}
		}
	}
	if (recipients.length > 0) {
		reviewInfo.properties['lecm-review-info:review-state'] = 'NOT_REVIEWED';
		reviewInfo.properties['lecm-review-info:review-start-date'] = new Date();
		reviewInfo.save();

		notifications.sendNotificationFromCurrentUser({
			recipients: recipients,
			templateCode: 'REVIEW_NEED',
			templateConfig: {
				mainObject: document,
				eventExecutor: initiator
			}
		});
		businessJournal.log(document.nodeRef.toString(), 'SEND_TO_REVIEW', 'Документ #mainobject направлен на ознакомление пользователем #initiator', []);
	}
}
