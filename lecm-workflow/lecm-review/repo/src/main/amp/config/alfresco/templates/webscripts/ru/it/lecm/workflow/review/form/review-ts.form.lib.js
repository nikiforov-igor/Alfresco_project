/* global logger, search, orgstructure */

function createReviewTSItem(persistedObject) {
	try {
		var reviewItem = search.findNode(persistedObject);
		var currentEmployee = orgstructure.getCurrentEmployee();
		if (reviewItem.typeShort == 'lecm-review-ts:review-table-item') {
                    reviewItem.createAssociation(currentEmployee, 'lecm-review-ts:initiator-assoc');
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
