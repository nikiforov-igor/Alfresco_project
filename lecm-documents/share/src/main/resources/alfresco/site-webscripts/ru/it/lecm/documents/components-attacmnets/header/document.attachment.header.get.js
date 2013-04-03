<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
	var hasViewListPerm = false;

	var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
	if (nodeDetails)
	{
		model.item = nodeDetails.item;
		model.node = nodeDetails.item.node;

		var document = getDocumentByAttachments(model.nodeRef);
		if (document != null && document.nodeRef != null && document.nodeRef.length > 0) {
			hasViewListPerm = hasPermission(document.nodeRef, PERM_CONTENT_LIST);

			model.documentNodeRef = document.nodeRef;
			var presentString = document.presentString;
	        if (presentString != null) {
	            model.documentName = presentString;
	        } else {
	            model.documentName = document.name;
	        }
		}
	}
	model.hasViewListPerm = hasViewListPerm;
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

main();