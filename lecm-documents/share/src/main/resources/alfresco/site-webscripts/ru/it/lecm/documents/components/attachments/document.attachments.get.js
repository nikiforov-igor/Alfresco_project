<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
    AlfrescoUtil.param("nodeRef");
    AlfrescoUtil.param("view", "");

	model.attachments = getAttachments(model.nodeRef);
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
