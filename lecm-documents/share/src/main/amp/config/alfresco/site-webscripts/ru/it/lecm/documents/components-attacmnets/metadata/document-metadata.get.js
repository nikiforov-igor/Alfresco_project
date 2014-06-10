<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
	AlfrescoUtil.param('nodeRef');
	AlfrescoUtil.param('site', null);
	AlfrescoUtil.param('formId', null);
	var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
	if (documentDetails) {
		model.document = documentDetails;

		var category = getCategoryByAttachments(model.nodeRef);
		model.allowMetaDataUpdate = category != null && !category.isReadOnly;
	}
}

function getCategoryByAttachments(nodeRef, defaultValue) {
	var url = '/lecm/document/attachments/api/getCategoryByAttachment?nodeRef=' + nodeRef;
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		if (defaultValue !== undefined) {
			return defaultValue;
		}
		return null;
	}
	return eval('(' + result + ')');
}

main();
