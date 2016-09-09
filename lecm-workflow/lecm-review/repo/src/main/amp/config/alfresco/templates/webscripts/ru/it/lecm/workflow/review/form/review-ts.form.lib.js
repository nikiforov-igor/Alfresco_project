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

	function pushEmployeeIfNotExist(employees, employee) {
		if (employees.indexOf('' + employee.nodeRef.toString()) == -1) {
			employees.push('' + employee.nodeRef.toString());
		}
	}

	// надо достать имплоев из ассоков и для каждого склепать итем
	var i, j, k, item,
		items = [],
		employee,
		employees = [],
		assocs,
		assocEmployees,
		currentEmployee = orgstructure.getCurrentEmployee(),
		allRows;

	for (i in reviewTsItems) {
		item = reviewTsItems[i];
		if ('lecm-orgstr:employee' == item.typeShort) {
			pushEmployeeIfNotExist(employees, item);
		} else if ('lecm-review-list:review-list-item' == item.typeShort) {
			assocs = item.assocs['lecm-review-list:reviewer-assoc'];
			if (assocs && assocs.length) {
				for (j in assocs) {
					if ('lecm-orgstr:employee' == assocs[j].typeShort) {
						pushEmployeeIfNotExist(employees, assocs[j]);
					} else if ('lecm-orgstr:organization-unit' == assocs[j].typeShort) {
						assocEmployees = orgstructure.getEmployeesInUnit(assocs[j]);
                        for (k in assocEmployees) {
							pushEmployeeIfNotExist(employees, assocEmployees[k]);
						}
					} else if ('lecm-orgstr:workGroup' == assocs[j].typeShort) {
						assocEmployees = orgstructure.getWorkGroupEmployees(assocs[j].nodeRef.toString());
						for (k in assocEmployees) {
							pushEmployeeIfNotExist(employees, assocEmployees[k]);
						}
					}
				}
			}
		} else if ('lecm-orgstr:organization-unit' == item.typeShort) {
			assocEmployees = orgstructure.getEmployeesInUnit(item);
			for (k in assocEmployees) {
				pushEmployeeIfNotExist(employees, assocEmployees[k]);
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
		row,
		startDate = new Date(),
		dueDate;

	for each (row in items) {
		reviewer = row.associations['lecm-review-ts:reviewer-assoc'][0];
		itemInitiator = row.assocs['lecm-review-ts:initiator-assoc'];
		if (itemInitiator && itemInitiator.length && initiator.nodeRef.equals(itemInitiator[0].nodeRef)) {
			if (row.properties['lecm-review-ts:review-state'] == 'NOT_STARTED') {
				documentMembers.addMemberWithoutCheckPermission(document, reviewer, 'LECM_BASIC_PG_Reader', true);
				recipients.push(reviewer);
				row.properties['lecm-review-ts:review-state'] = 'NOT_REVIEWED';
				row.properties['lecm-review-ts:review-start-date'] = startDate;
				row.save();
			}
		}
	}
	if (recipients.length > 0) {
		
		dueDate = workCalendar.getNextWorkingDateByDays(startDate, review.getReviewTerm());
		
		notifications.sendNotificationFromCurrentUser({
			recipients: recipients,
			templateCode: 'REVIEW_NEED',
			templateConfig: {
				mainObject: document,
				eventExecutor: initiator,
				dueDate: dueDate
			}
		});
	}
}
