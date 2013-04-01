<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
	AlfrescoUtil.param("nodeRef");

	model.hasViewListPerm = hasViewAttachmentsListPermission(model.nodeRef);
	model.hasViewAttachmentPerm = hasViewAttachmentPermission(model.nodeRef);
	if (model.hasViewListPerm) {
		model.categories = getCategories(model.nodeRef).categories;
	}
}

function getCategories(nodeRef, defaultValue) {
	var url = '/lecm/document/attachments/api/categories?documentNodeRef=' + nodeRef;
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		if (defaultValue !== undefined) {
			return defaultValue;
		}
		AlfrescoUtil.error(result.status, 'Could not get attachments for node ' + nodeRef);
	}
	return eval('(' + result + ')');
}

function hasViewAttachmentsListPermission(nodeRef) {
	var url = '/lecm/security/api/getPermission?nodeRef=' + nodeRef + '&permission=_lecmPerm_ContentList';
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		return false;
	}
	var permission = eval('(' + result + ')');
	return (("" + permission) ==  "true");
}

function hasViewAttachmentPermission(nodeRef) {
	var url = '/lecm/security/api/getPermission?nodeRef=' + nodeRef + '&permission=_lecmPerm_ContentView';
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		return false;
	}
	var permission = eval('(' + result + ')');
	return (("" + permission) ==  "true");
}

main();