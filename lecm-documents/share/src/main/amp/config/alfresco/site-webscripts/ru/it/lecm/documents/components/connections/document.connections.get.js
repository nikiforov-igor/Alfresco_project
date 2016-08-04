<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">
function main() {
    AlfrescoUtil.param("nodeRef");
	var hasPerm = hasPermission(model.nodeRef, PERM_LINKS_VIEW);
	if (hasPerm) {
        var conns = getConnections(model.nodeRef);
        if (conns != null) {
		    model.connections = conns;
        }
	}
}

function getConnections(nodeRef, defaultValue) {
    var url = '/lecm/document/connections/api/records?documentNodeRef=' + nodeRef + "&count=5&checkAccess=true&checkInAccess=false&applyViewMode=false";
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
