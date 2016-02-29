<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/repository/forms/form.post.js">
<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/workflow/review/form/review-ts.form.lib.js">

if (status.code == status.STATUS_OK && model.persistedObject) {
	logger.log('before: '+model.persistedObject.toString());
	createReviewTSItem(model.persistedObject);
	logger.log('after: '+model.persistedObject.toString());
}
