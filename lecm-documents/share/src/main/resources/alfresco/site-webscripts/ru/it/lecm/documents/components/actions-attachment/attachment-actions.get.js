<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

var showActions = [
	"document-download",
	"document-view-content",
	"document-edit-metadata",
	"document-inline-edit",
	"document-edit-online",
	"document-edit-offline",
	"document-view-working-copy",
	"document-cancel-editing"
];

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   AlfrescoUtil.param('container', 'documentLibrary');
	args.view = "attachment";

   var documentDetails = DocumentUtils.getNodeDetails(model.nodeRef, model.site,
   {
      actions: true
   });
   if (documentDetails)
   {
	  setCheckedActions();

	  documentDetails.item.actions = getShowAction(documentDetails.item.actions);
	  model.documentDetailsJSON = jsonUtils.toJSONString(documentDetails);
      doclibCommon();

	   if (documentDetails.item.parent != null) {
		   var attachmentsFolder = DocumentUtils.getNodeDetails(documentDetails.item.parent.nodeRef);
		   if (attachmentsFolder != null && attachmentsFolder.item.parent != null) {
			   var categoryFolder = DocumentUtils.getNodeDetails(attachmentsFolder.item.parent.nodeRef);
			   if (categoryFolder != null && categoryFolder.item.parent != null){
				   var documentFolder = DocumentUtils.getNodeDetails(categoryFolder.item.parent.nodeRef);
				   if (documentFolder != null) {
					   model.documentNodeRef = documentFolder.item.node.nodeRef;
					   var presentString = documentFolder.item.node.properties["lecm-document:present-string"];
					   if (presentString != null) {
						   model.documentName = presentString;
					   } else {
						   model.documentName = documentFolder.item.displayName;
					   }
				   }
			   }
		   }
	   }
   }
}

function setCheckedActions() {
	if (hasPermission(model.nodeRef, '_lecmPerm_ContentAddVer')) {
		showActions.push("document-upload-new-version");
	}

	if (hasPermission(model.nodeRef, '_lecmPerm_ContentCopy')) {
		showActions.push("document-copy-to");
	}

	if (hasPermission(model.nodeRef, '_lecmPerm_ContentDelete')) {
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