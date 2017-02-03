<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">
function main() {
    AlfrescoUtil.param("nodeRef");
	var excludeType = AlfrescoUtil.param("excludeType");
	var hasPerm = hasPermission(model.nodeRef, PERM_LINKS_VIEW);
	if (hasPerm) {
        var conns = getConnections(model.nodeRef, null, excludeType);
        if (conns) {
		    model.connections = conns;
        }
	}
}

function getConnections(nodeRef, defaultValue, excludeType) {
    var url = '/lecm/document/connections/api/records?documentNodeRef=' + nodeRef + "&count=5&applyViewMode=false";
	if (excludeType) {
		url += '&excludeType=' + encodeURIComponent(excludeType);
	}
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
