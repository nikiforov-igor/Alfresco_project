<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");

	model.hasViewListPerm = hasPermission(model.nodeRef, PERM_CONTENT_LIST);
	model.hasViewAttachmentPerm = hasPermission(model.nodeRef, PERM_CONTENT_VIEW);
	model.hasAddAttachmentPerm = hasPermission(model.nodeRef, PERM_CONTENT_ADD);

	var allActions = [];
	model.readOnlyActions = [
		"document-download",
		"document-view-content"
	];

	if (model.hasViewListPerm) {
        model.categories = getCategories(model.nodeRef).categories;
	}

	if (model.hasViewAttachmentPerm) {
		allActions.push("document-download");
		allActions.push("document-view-content");
		allActions.push("document-edit-properties");
	}

	if (hasPermission(model.nodeRef, PERM_CONTENT_ADD_VER)) {
		allActions.push("document-upload-new-version");
	}

	if (hasPermission(model.nodeRef, PERM_CONTENT_DELETE)) {
		allActions.push("document-delete");
	}

	model.allActions = allActions;
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

main();