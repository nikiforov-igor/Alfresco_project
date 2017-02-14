/* global logger, search, orgstructure, documentMembers, notifications, businessJournal, review, documentTables, utils */
function createReviewTSItem(reviewTable, reviewTsItems, initiatingDocument) {

	function getOnReviewRecord(allRows, employee) {
		var i, j, row, state, reviewers;
		for (i in allRows) {
			row = allRows[i];
			state = '' + row.properties['lecm-review-ts:review-state'];
			if ('NOT_REVIEWED' == state) {
				reviewers = row.assocs['lecm-review-ts:reviewer-assoc'];
				if (reviewers && reviewers.length) {
					for (j in reviewers) {
						if ('' + reviewers[j].nodeRef.toString() == employee) {
							return row;
						}
					}
				}
			}
		}
		return null;
	}

	function pushEmployeeIfNotExist(employees, employee) {
		if (employees.indexOf('' + employee.nodeRef.toString()) == -1) {
			employees.push('' + employee.nodeRef.toString());
		}
	}

	function pushEmployeeByType(employees, item) {
		var assocEmployees;
		var typeShort = item.typeShort;

		if ('lecm-orgstr:employee' == typeShort) {
			pushEmployeeIfNotExist(employees, item);
		} else if ('lecm-orgstr:organization-unit' == typeShort) {
			assocEmployees = orgstructure.getEmployeesInUnit(item);
			for (k in assocEmployees) {
				pushEmployeeIfNotExist(employees, assocEmployees[k]);
			}
		} else if ('lecm-orgstr:workGroup' == typeShort) {
			assocEmployees = orgstructure.getWorkGroupEmployees(item.nodeRef.toString());
			for (k in assocEmployees) {
				pushEmployeeIfNotExist(employees, assocEmployees[k]);
			}
		} else if ('lecm-review-list:review-list-item' == typeShort) {
			assocs = item.assocs['lecm-review-list:reviewer-assoc'];
			if (assocs && assocs.length) {
				for (j in assocs) {
					pushEmployeeByType(employees, assocs[j]);
				}
			}
		}
	}

	// надо достать имплоев из ассоков и для каждого склепать итем
	var i, j, k,
		items = [],
		employee,
		employees = [],
		assocs,
		currentEmployee = orgstructure.getCurrentEmployee(),
		allRows;

	for (i in reviewTsItems) {
		pushEmployeeByType(employees, reviewTsItems[i]);
	}
	for (i in employees) {
		allRows = documentTables.getTableDataRows(reviewTable.nodeRef.toString());
		var reviewRecord = getOnReviewRecord(allRows, employees[i]);
		if (reviewRecord === null) {
			employee = utils.getNodeFromString(employees[i]);
            reviewRecord = reviewTable.createNode(null, 'lecm-review-ts:review-table-item', {
				'lecm-document:indexTableRow': allRows.length ? allRows.length + 1 : 1
			});
            reviewRecord.createAssociation(currentEmployee, 'lecm-review-ts:initiator-assoc');
            reviewRecord.createAssociation(employee, 'lecm-review-ts:reviewer-assoc');
			items.push(reviewRecord);
		}
        if (reviewRecord && initiatingDocument) {
            var existAssocs = initiatingDocument.assocs["lecm-review-aspects:related-review-records-assoc"];
            var existReviewRecordAssoc = existAssocs && existAssocs.some(function (assoc) {
                return assoc.equals(reviewRecord);
            });
            if (!existReviewRecordAssoc) {
                initiatingDocument.createAssociation(reviewRecord, 'lecm-review-aspects:related-review-records-assoc');
            }
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

function sendDocumentToReview(document, reviewers, initiatingDocument) {
    var reviewTable = document.associations['lecm-review-ts:review-table-assoc'];

    if (reviewTable && reviewTable.length) {
        var reviewTsItems = createReviewTSItem(reviewTable[0], reviewers, initiatingDocument);
        sendToReview(reviewTsItems);
    }
}
