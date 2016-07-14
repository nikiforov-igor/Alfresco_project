/* global logger, search, orgstructure, documentMembers, notifications, businessJournal, review, documentTables, utils */
function createReviewTSItem(reviewTable, reviewTsItems) {

	function isOnReview(allRows, employee) {
		var i, j, row, state, reviewers, isOnReview = false;
		for (i in allRows) {
			row = allRows[i];
			state = '' + row.properties['lecm-review-ts:review-state'];
			if ('NOT_REVIEWED' == state || 'REVIEWED' == state) {
				reviewers = row.assocs['lecm-review-ts:reviewer-assoc'];
				if (reviewers && reviewers.length) {
					for (j in reviewers) {
						if ('' + reviewers[j].nodeRef.toString() == employee) {
							isOnReview = true;
							break;
						}
					}
				}
			}
			if (isOnReview) {
				break;
			}
		}
		return isOnReview;
	}
	// надо достать имплоев из ассоков и для каждого склепать итем
	var i, j, item,
		items = [],
		employee,
		employees = [],
		assocs,
		currentEmployee = orgstructure.getCurrentEmployee(),
		allRows;

	for (i in reviewTsItems) {
		item = reviewTsItems[i];
		if ('lecm-orgstr:employee' == item.typeShort && employees.indexOf('' + item.nodeRef.toString()) == -1) {
			employees.push('' + item.nodeRef.toString());
		}
		if ('lecm-review-list:review-list-item' == item.typeShort) {
			assocs = item.assocs['lecm-review-list:reviewer-assoc'];
			if (assocs && assocs.length) {
				for (j in assocs) {
					if (employees.indexOf('' + assocs[j].nodeRef.toString()) == -1) {
						employees.push('' + assocs[j].nodeRef.toString());
					}
				}
			}
		}
	}
	for (i in employees) {
		allRows = documentTables.getTableDataRows(reviewTable.nodeRef.toString());
		if (!isOnReview(allRows, employees[i])) {
			employee = utils.getNodeFromString(employees[i]);
			item = reviewTable.createNode(null, 'lecm-review-ts:review-table-item', {
				'lecm-document:indexTableRow': allRows.length ? allRows.length + 1 : 1
			});
			item.createAssociation(currentEmployee, 'lecm-review-ts:initiator-assoc');
			item.createAssociation(employee, 'lecm-review-ts:reviewer-assoc');
			items.push(item);
		}
	}
	return items;
}

function sendToReview(items) {
	var initiator = orgstructure.getCurrentEmployee(),
		recipients = [],
		reviewer,
		itemInitiator,
		row;

	for each (row in items) {
		reviewer = row.associations['lecm-review-ts:reviewer-assoc'][0];
		itemInitiator = row.assocs['lecm-review-ts:initiator-assoc'];
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
		notifications.sendNotificationFromCurrentUser({
			recipients: recipients,
			templateCode: 'REVIEW_NEED',
			templateConfig: {
				mainObject: document,
				eventExecutor: initiator
			}
		});
	}
}
