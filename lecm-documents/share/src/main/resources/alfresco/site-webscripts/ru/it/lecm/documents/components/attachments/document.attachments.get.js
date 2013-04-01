<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
    AlfrescoUtil.param("nodeRef");
    AlfrescoUtil.param("view", "");
	var hasPerm = hasViewAttachmentsListPermission(model.nodeRef);
	if (hasPerm) {
		model.attachments = getAttachments(model.nodeRef);
	}
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

function getAttachments(nodeRef, defaultValue) {
	var url = '/lecm/document/attachments/api/get?documentNodeRef=' + nodeRef + "&count=5";
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		if (defaultValue !== undefined) {
			return defaultValue;
		}
		AlfrescoUtil.error(result.status, 'Could not get connections for node ' + nodeRef);
	}
	return eval('(' + result + ')');
}

main();
