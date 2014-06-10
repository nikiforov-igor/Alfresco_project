<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main()
{
    AlfrescoUtil.param("nodeRef");
    AlfrescoUtil.param("view", "");
	var hasPerm = hasPermission(model.nodeRef, PERM_CONTENT_LIST);
	if (hasPerm) {
        var atts = getAttachments(model.nodeRef);
        if (atts != null) {
            model.attachments = atts;
        }
		model.hasViewAttachmentPerm = hasPermission(model.nodeRef, PERM_CONTENT_VIEW);
	}
}

function getAttachments(nodeRef, defaultValue) {
	var url = '/lecm/document/attachments/api/get?documentNodeRef=' + nodeRef + "&count=5";
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
