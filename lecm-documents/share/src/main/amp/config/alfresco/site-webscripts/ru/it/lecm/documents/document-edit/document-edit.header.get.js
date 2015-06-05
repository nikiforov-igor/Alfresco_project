<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">
<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    var nodeDetails = DocumentUtils.getNodeDetails(model.nodeRef);
	var hasPerm = hasPermission(model.nodeRef, PERM_ATTR_EDIT);
	model.hasPerm = hasPerm;

	url = '/lecm/document/api/url/view?nodeRef=' + encodeURI(model.nodeRef);
	var viewUrl = remote.connect("alfresco").get(url);
	if (viewUrl.status == 200) {
		var result = eval('(' + viewUrl + ')');
		model.viewUrl = result.url;
	}

    if (!nodeDetails || !hasPerm) {
		var accessInfo = DocumentUtils.getNodeAccess(model.nodeRef, user.id);
		if (accessInfo) {
			if (accessInfo.exists) {
				model.accessMsg = "msg.access_denied";
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
}

main();