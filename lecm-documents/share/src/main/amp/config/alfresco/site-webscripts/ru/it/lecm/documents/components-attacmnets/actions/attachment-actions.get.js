<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

var showActions = [
	"document-download",
	"document-view-content",
	"document-unlock"
];

function main() {
	AlfrescoUtil.param('nodeRef');
	AlfrescoUtil.param('site', null);
	AlfrescoUtil.param('container', 'documentLibrary');
	args.view = "details";

	var attachmentDetails = DocumentUtils.getNodeDetails(model.nodeRef, model.site,
		{
			actions: true
		});
	if (attachmentDetails) {
		var category = getCategoryByAttachments(model.nodeRef);
		model.document = getDocumentByAttachments(model.nodeRef);

		setCheckedActions(model.document, category, attachmentDetails);

		attachmentDetails.item.actions = getShowAction(attachmentDetails.item.actions);
		model.attachmentDetailsJSON = jsonUtils.toJSONString(attachmentDetails);
		doclibCommon();
	}
}

function getDocumentByAttachments(nodeRef, defaultValue) {
	var url = '/lecm/document/attachments/api/getDocumentByAttachment?nodeRef=' + nodeRef;
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		if (defaultValue !== undefined) {
			return defaultValue;
		}
        return {};
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

function setCheckedActions(document, category, nodeDetails) {
	if (category != null && !category.isReadOnly) {
		showActions.push("document-edit-metadata");
		showActions.push("document-edit-properties");
		showActions.push("document-inline-edit");
		showActions.push("document-edit-online");
		showActions.push("document-edit-offline");
		showActions.push("document-view-working-copy");
		showActions.push("document-cancel-editing");
	}

	if (hasPermission(document.nodeRef, PERM_CONTENT_ADD_VER) && category != null && !category.isReadOnly) {
		showActions.push("document-upload-new-version");
	}

	if (hasPermission(document.nodeRef, PERM_CONTENT_COPY) && category != null && !category.isReadOnly) {
		showActions.push("document-copy-to");
	}

	var nodeCreator = null;
	if (nodeDetails != null && nodeDetails.item != null && nodeDetails.item.node != null) {
		nodeCreator = nodeDetails.item.node.properties["cm:creator"];
	}

	if ((hasPermission(document.nodeRef, PERM_CONTENT_DELETE)
		|| (hasPermission(document.nodeRef, PERM_OWN_CONTENT_DELETE) && nodeCreator!= null && nodeCreator.userName == user.name))
		&& category != null && !category.isReadOnly) {
		showActions.push("document-delete");
	}
}

function getShowAction(actions) {
	var result = [];
	for (var i = 0; i < actions.length; i++) {
		if (showAction(actions[i].id)) {
			result.push(actions[i]);
		}
	}
	return result;
}

function showAction(action) {
	for (var i = 0; i < showActions.length; i++) {
		if (action == showActions[i]) {
			return true;
		}
	}
	return false;
}

main();