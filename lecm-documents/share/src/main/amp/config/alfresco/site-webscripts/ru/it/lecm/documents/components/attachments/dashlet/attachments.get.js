<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
	AlfrescoUtil.param("nodeRef");
	var baseDocAssocName = AlfrescoUtil.param("baseDocAssocName", null);
	var showBaseDocAttachmentsBottom = AlfrescoUtil.param("showBaseDocAttachmentsBottom", null);

	model.hasViewListPerm = hasPermission(model.nodeRef, PERM_CONTENT_LIST);
	model.hasViewAttachmentPerm = hasPermission(model.nodeRef, PERM_CONTENT_VIEW);
	model.hasAddNewVersionAttachmentPerm = hasPermission(model.nodeRef, PERM_CONTENT_ADD_VER);
	model.hasDeleteAttachmentPerm = hasPermission(model.nodeRef, PERM_CONTENT_DELETE);
	model.hasDeleteOwnAttachmentPerm = hasPermission(model.nodeRef, PERM_OWN_CONTENT_DELETE);
	model.lockedAttacments = getLockedAttachments(model.nodeRef, 50, baseDocAssocName, showBaseDocAttachmentsBottom);
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

function getLockedAttachments(nodeRef, count, baseDocAssocName, showBaseDocAttachmentsBottom) {
	var i, j, category, attachment;
	var lockedAttachments = [];
	var url = '/lecm/document/attachments/api/get' + '?documentNodeRef=' + nodeRef + '&count=' + count;
	if (baseDocAssocName) {
		url += '&baseDocAssocName=' + encodeURIComponent(baseDocAssocName);
		url += '&showBaseDocAttachmentsBottom=' + encodeURIComponent(showBaseDocAttachmentsBottom);
	}
	var result = remote.connect("alfresco").get(url);
	if (result.status == 200) {
		var data = eval('(' + result + ')');
		for (i = 0; i < data.items.length; i++) {
			category = data.items[i];
			for (j = 0; j < category.attachments.length; j++) {
				attachment = category.attachments[j];
				if (attachment.locked) {
					lockedAttachments.push(attachment.nodeRef);
				}
			}
		}
	}
	return lockedAttachments;
}

main();
