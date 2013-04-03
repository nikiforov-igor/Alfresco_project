<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
	AlfrescoUtil.param('nodeRef');
	AlfrescoUtil.param('site', null);
	AlfrescoUtil.param('container', 'documentLibrary');
	var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
	if (documentDetails) {
		var category = getCategoryByAttachments(model.nodeRef);
		var document = getDocumentByAttachments(model.nodeRef)

		model.allowNewVersionUpload = hasPermission(document.nodeRef, PERM_CONTENT_ADD_VER) && category != null && !category.isReadOnly;
		if (documentDetails.workingCopy && documentDetails.workingCopy.workingCopyVersion) {
			model.workingCopyVersion = documentDetails.workingCopy.workingCopyVersion;
		}
	}
}

function getDocumentByAttachments(nodeRef, defaultValue) {
	var url = '/lecm/document/attachments/api/getDocumentByAttachment?nodeRef=' + nodeRef;
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		if (defaultValue !== undefined) {
			return defaultValue;
		}
		AlfrescoUtil.error(result.status, 'Could not get connections for node ' + nodeRef);
	}
	return eval('(' + result + ')');
}

function getCategoryByAttachments(nodeRef, defaultValue) {
	var url = '/lecm/document/attachments/api/getCategoryByAttachment?nodeRef=' + nodeRef;
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		if (defaultValue !== undefined) {
			return defaultValue;
		}
		AlfrescoUtil.error(result.status, 'Could not get connections for node ' + nodeRef);
	}
	return eval('(' + result + ')');
}

main();
