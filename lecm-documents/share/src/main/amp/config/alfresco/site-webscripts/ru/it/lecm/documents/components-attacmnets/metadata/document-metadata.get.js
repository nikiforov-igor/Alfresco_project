<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
	AlfrescoUtil.param('nodeRef');
	AlfrescoUtil.param('site', null);
	AlfrescoUtil.param('formId', null);
	var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site);
	if (documentDetails) {
		model.document = documentDetails;

		if (documentDetails.item.node.properties["cm:workingCopyMode"] == "offlineEditing") {
			model.allowMetaDataUpdate = false;
			return;
		}
		var category = getCategoryByAttachments(model.nodeRef);
		var mayEditMetadata = hasPermission(model.nodeRef, PERM_ATTR_EDIT);
		model.allowMetaDataUpdate = category != null && !category.isReadOnly && mayEditMetadata;
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
