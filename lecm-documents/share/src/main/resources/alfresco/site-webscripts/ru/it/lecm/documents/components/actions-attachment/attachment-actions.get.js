<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

var showActions = [
	"document-download",
	"document-view-content"
];

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   AlfrescoUtil.param('container', 'documentLibrary');
	args.view = "attachment";

   var attachmentDetails = DocumentUtils.getNodeDetails(model.nodeRef, model.site,
   {
      actions: true
   });
   if (attachmentDetails) {
	   var category = getCategoryByAttachments(model.nodeRef);

	   setCheckedActions(category);

	   attachmentDetails.item.actions = getShowAction(attachmentDetails.item.actions);
	   model.attachmentDetailsJSON = jsonUtils.toJSONString(attachmentDetails);
       doclibCommon();

	   model.document = getDocumentByAttachments(model.nodeRef);
   }
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

function getCategoryByAttachments(nodeRef, defaultValue) {
	var url = '/lecm/document/attachments/api/getCategoryByAttachment?nodeRef=' + nodeRef;
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		if (defaultValue !== undefined) {
			return defaultValue;
		}
		AlfrescoUtil.error(result.status, 'Could not get connections for node ' + nodeRef);
	}
	return eval('(' + result + ')');
}

function setCheckedActions(category) {
	if (category != null && !category.isReadOnly) {
		showActions.push("document-edit-metadata");
		showActions.push("document-edit-properties");
		showActions.push("document-inline-edit");
		showActions.push("document-edit-online");
		showActions.push("document-edit-offline");
		showActions.push("document-view-working-copy");
		showActions.push("document-cancel-editing");
	}

	if (hasPermission(model.nodeRef, PERM_CONTENT_ADD_VER) && category != null && !category.isReadOnly) {
		showActions.push("document-upload-new-version");
	}

	if (hasPermission(model.nodeRef, PERM_CONTENT_COPY) && category != null && !category.isReadOnly) {
		showActions.push("document-copy-to");
	}

	if (hasPermission(model.nodeRef, PERM_CONTENT_DELETE) && category != null && !category.isReadOnly) {
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