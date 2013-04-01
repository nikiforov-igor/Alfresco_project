<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main()
{
    AlfrescoUtil.param("nodeRef");
    AlfrescoUtil.param("view", "");
	var hasPerm = hasPermission(model.nodeRef, '_lecmPerm_ContentList');;
	if (hasPerm) {
		model.attachments = getAttachments(model.nodeRef);
	}
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
