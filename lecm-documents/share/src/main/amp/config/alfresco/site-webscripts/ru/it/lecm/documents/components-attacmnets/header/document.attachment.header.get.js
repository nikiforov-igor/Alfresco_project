<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
	var hasViewListPerm = false;

	var rAttachmentPermission = hasReadAttachmentPermission(model.nodeRef,user.id);
	var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
	if (nodeDetails && rAttachmentPermission)
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
			model.allAttachments = getAllDocumentAttachments(document.nodeRef);
		}
	} else {
		var accessInfo = DocumentUtils.getNodeAccess(model.nodeRef, user.id);
		if (accessInfo) {
			if (accessInfo.exists) {
				if (!accessInfo.hasReadPermissions || !rAttachmentPermission) {
					model.accessMsg = "msg.access_denied";
				}
			} else {
				if (accessInfo.removed) {
					model.accessMsg = "msg.document_removed";
				} else {
					model.accessMsg = "msg.no_such_document";
				}
			}
		} else {
			model.accessMsg = "msg.document_not_found";
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
		return null;
	}
	return eval('(' + result + ')');
}

function getAllDocumentAttachments(nodeRef, defaultValue) {
	var url = '/lecm/document/attachments/api/get?documentNodeRef=' + nodeRef + "&showEmptyCategory=true";
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