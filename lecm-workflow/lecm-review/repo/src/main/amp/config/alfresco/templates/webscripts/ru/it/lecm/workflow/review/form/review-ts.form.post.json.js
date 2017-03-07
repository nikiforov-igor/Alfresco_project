<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/repository/forms/form.post.json.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/workflow/review/form/review-ts.form.lib.js">

var i,
	reviewInfo,
	tmpTsItems = [],
	reviewTsItems = [];
if (status.code == status.STATUS_OK && model.persistedObject) {
//	createReviewTSItem(model.persistedObject);
	reviewInfo = utils.getNodeFromString(model.persistedObject);
	if (json.has('lecm-review-ts_reviewer-assoc_added')) {
		tmpTsItems = (json.get('lecm-review-ts_reviewer-assoc_added') || '').split(',');
		for (i in tmpTsItems) {
			reviewTsItems.push(utils.getNodeFromString(tmpTsItems[i]));
		}
	}
	reviewTsItems = createReviewTSItem(reviewInfo, reviewTsItems);
	sendToReview(reviewInfo);
}
