<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
	AlfrescoUtil.param("nodeRef");

	model.hasViewListPerm = hasPermission(model.nodeRef, PERM_CONTENT_LIST);
	model.hasViewAttachmentPerm = hasPermission(model.nodeRef, PERM_CONTENT_VIEW);
	model.hasAddNewVersionAttachmentPerm = hasPermission(model.nodeRef, PERM_CONTENT_ADD_VER);
	model.hasDeleteAttachmentPerm = hasPermission(model.nodeRef, PERM_CONTENT_DELETE);
	model.hasDeleteOwnAttachmentPerm = hasPermission(model.nodeRef, PERM_OWN_CONTENT_DELETE);
	if (model.hasViewListPerm) {
	    var cats = getCategories(model.nodeRef);
        if (cats != null) {
            model.categories = cats.categories;
        }
	}
}

function getCategories(nodeRef, defaultValue) {
	var url = '/lecm/document/attachments/api/categories?documentNodeRef=' + nodeRef;
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