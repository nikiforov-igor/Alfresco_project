/* global logger, search, orgstructure */

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
			logger.log("Returning 500 status code");
		}
	}
}

function createReviewTSItem(reviewInfo, reviewTsItems) {
	// надо достать имплоев из ассоков и для каждого склепать итем
	var i, j, item,
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
	}
}
