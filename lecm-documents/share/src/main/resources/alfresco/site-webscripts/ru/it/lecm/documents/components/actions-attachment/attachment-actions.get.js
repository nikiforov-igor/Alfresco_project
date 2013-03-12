<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/include/documentlist.lib.js">

function main()
{
   AlfrescoUtil.param('nodeRef');
   AlfrescoUtil.param('site', null);
   AlfrescoUtil.param('container', 'documentLibrary');
	args.view = "attachment";

   var documentDetails = AlfrescoUtil.getNodeDetails(model.nodeRef, model.site,
   {
      actions: true
   });
   if (documentDetails)
   {
	  documentDetails.item.actions = getShowAction(documentDetails.item.actions);
	  model.documentDetailsJSON = jsonUtils.toJSONString(documentDetails);
      doclibCommon();
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
	var showActions = [
		"document-download",
		"document-view-content",
		"document-edit-metadata",
		"document-upload-new-version",
		"document-inline-edit",
		"document-edit-online",
		"document-edit-offline",
		"document-view-working-copy",
		"document-cancel-editing",
		"document-copy-to",
		"document-delete"
	];
	for (var i = 0; i < showActions.length; i++) {
		if (action == showActions[i]) {
			return true;
		}
	}
	return false;
}

main();