<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
	AlfrescoUtil.param("nodeRef");
	var category = getCategoryByAttachments(model.nodeRef);
	if (category != null && !category.isReadOnly) {
		var document = getDocumentByAttachments(model.nodeRef);
		if (document != null && hasPermission(document.nodeRef, PERM_CONTENT_ADD_VER)) {
			var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
			if (nodeDetails) {
				model.item = nodeDetails.item;
				model.node = nodeDetails.item.node;
			}
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
		return null;
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
		return null;
	}
	return eval('(' + result + ')');
}

main();