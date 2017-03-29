<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
	var baseDocAssocName = AlfrescoUtil.param("baseDocAssocName", null);
	var showBaseDocAttachmentsBottom = AlfrescoUtil.param("showBaseDocAttachmentsBottom", "false").toLowerCase() == "true";
	var hasViewListPerm = false;
	var isMlSupported;
	var mlValue;
	var presentString;

	var rAttachmentPermission = hasReadAttachmentPermission(model.nodeRef,user.id);
	var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
	if (nodeDetails && rAttachmentPermission)
	{
		model.item = nodeDetails.item;
		model.node = nodeDetails.item.node;

		var document = getDocumentByAttachments(model.nodeRef);
		if (document != null && document.nodeRef != null && document.nodeRef.length > 0) {
			isMlSupported = nodeDetails.isMlSupported;
			mlValue = document.mlPresentString;
			presentString = isMlSupported && mlValue ? mlValue : document.presentString;
			hasViewListPerm = hasPermission(document.nodeRef, PERM_CONTENT_LIST);

			model.documentNodeRef = document.nodeRef;
			model.documentName = presentString ? presentString : document.name;
			model.allAttachments = getAllDocumentAttachments(document.nodeRef, null, baseDocAssocName, showBaseDocAttachmentsBottom);
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
	model.documentPageName = getDocumentPage(document.nodeRef) ? getDocumentPage(document.nodeRef).pageName : 'document';
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

function getAllDocumentAttachments(nodeRef, defaultValue, baseDocAssocName, showBaseDocAttachmentsBottom) {
	var url = '/lecm/document/attachments/api/get?documentNodeRef=' + nodeRef + "&showEmptyCategory=true";
	if (baseDocAssocName) {
		url += '&baseDocAssocName=' + encodeURIComponent(baseDocAssocName);
		url += '&showBaseDocAttachmentsBottom=' + encodeURIComponent(showBaseDocAttachmentsBottom);
	}
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		if (defaultValue !== undefined) {
			return defaultValue;
		}
		return null;
	}
	return eval('(' + result + ')');
}

function getDocumentPage(nodeRef) {
	var url = '/lecm/document/attachments/api/getDocumentPage?documentNodeRef=' + nodeRef;
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		return null;
	}
	return eval('(' + result + ')');
}

main();
