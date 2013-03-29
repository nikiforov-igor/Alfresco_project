<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    AlfrescoUtil.param("nodeRef");

	var hasPerm = hasViewConnectionsPermission(model.nodeRef);
	if (hasPerm) {
		model.connections = getConnections(model.nodeRef);
	}
}

function getConnections(nodeRef, defaultValue) {
    var url = '/lecm/document/connections/api/records?documentNodeRef=' + nodeRef + "&count=5";
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        if (defaultValue !== undefined) {
            return defaultValue;
        }
        AlfrescoUtil.error(result.status, 'Could not get connections for node ' + nodeRef);
    }
    return eval('(' + result + ')');
}

function hasViewConnectionsPermission(nodeRef) {
	var url = '/lecm/security/api/getPermission?nodeRef=' + nodeRef + '&permission=_lecmPerm_LinksView';
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		return false;
	}
	var permObj = eval('(' + result + ')');
	return (("" + permObj.hasPermission) ==  "true");
}

main();
