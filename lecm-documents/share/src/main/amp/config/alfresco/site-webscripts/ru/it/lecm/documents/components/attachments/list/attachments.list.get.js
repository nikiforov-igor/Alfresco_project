<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    model.baseDocAssocName = AlfrescoUtil.param("baseDocAssocName", null);

	model.hasViewListPerm = hasPermission(model.nodeRef, PERM_CONTENT_LIST);
	model.hasViewAttachmentPerm = hasPermission(model.nodeRef, PERM_CONTENT_VIEW);
	model.hasReadAttachmentPerm = hasPermission(model.nodeRef, PERM_READ_ATTACHMENT);
	model.hasAddAttachmentPerm = hasPermission(model.nodeRef, PERM_CONTENT_ADD);
	model.hasDeleteOwnAttachmentPerm = hasPermission(model.nodeRef, PERM_OWN_CONTENT_DELETE);
	model.hasStatemachine = hasStatemachine(model.nodeRef);

	var allActions = [];
	model.readOnlyActions = [
		{
			id: "document-download",
			onlyForOwn: false
		},
		{
			id: "document-view-content",
			onlyForOwn: false
		},
		{
			id: "document-unlock",
			onlyForOwn: false
		}
	];

	if (model.hasViewListPerm) {
        var cats = getCategories(model.nodeRef);
        if (cats != null) {
            model.categories = cats.categories;

	        if (model.baseDocAssocName) {
		        model.categories.push({
			        nodeRef: "base-document-attachments/" + model.nodeRef.replace(":/", "") + "/" + model.baseDocAssocName,
			        name: msg.get("label.attachments.base-document"),
			        path: "",
			        isReadOnly: true
		        });
			}
        }
	}

	if (model.hasViewAttachmentPerm) {
		allActions.push({
			id: "document-download",
			onlyForOwn: false
		});
		allActions.push({
			id: "document-view-content",
			onlyForOwn: false
		});
		allActions.push({
			id: "document-unlock",
			onlyForOwn: false
		});
		allActions.push({
			id: "lecm-online-editing",
			onlyForOwn: false
		});
		allActions.push({
			id: "document-edit-properties",
			onlyForOwn: false
		});
	}

	if (hasPermission(model.nodeRef, PERM_CONTENT_ADD_VER)) {
		allActions.push({
			id: "document-upload-new-version",
			onlyForOwn: false
		});
	}

	if (hasPermission(model.nodeRef, PERM_CONTENT_COPY)) {
		allActions.push({
			id: "move-to-another-category",
			onlyForOwn: false
		});
	}

	if (hasPermission(model.nodeRef, PERM_CONTENT_DELETE)) {
		allActions.push({
			id: "document-delete",
			onlyForOwn: false
		});
	} else if (hasPermission(model.nodeRef, PERM_OWN_CONTENT_DELETE)) {
		allActions.push({
			id: "document-delete",
			onlyForOwn: true
		});
	}

	model.allActions = allActions;
};

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
