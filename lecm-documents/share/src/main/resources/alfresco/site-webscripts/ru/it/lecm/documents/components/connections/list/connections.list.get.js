<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">
function main() {
    AlfrescoUtil.param("nodeRef");

	model.hasViewPerm = hasPermission(model.nodeRef, '_lecmPerm_LinksView');
	model.hasCreatePerm = hasPermission(model.nodeRef, '_lecmPerm_LinksCreate');
	model.hasDeletePerm = hasPermission(model.nodeRef, '_lecmPerm_LinksDelete');
	if (model.hasViewPerm) {
		model.connections = getConnections(model.nodeRef);
		model.connectionsWithDocument = getConnectionsWithDocument(model.nodeRef);
	}
}

function getConnections(nodeRef, defaultValue) {
	var url = '/lecm/document/connections/api/records?documentNodeRef=' + nodeRef + "&count=100";
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		if (defaultValue !== undefined) {
			return defaultValue;
		}
		AlfrescoUtil.error(result.status, 'Could not get connections for node ' + nodeRef);
	}
	return eval('(' + result + ')');
}

function getConnectionsWithDocument(nodeRef, defaultValue) {
	var url = '/lecm/document/connections/api/getConnectionsWithDocument?documentNodeRef=' + nodeRef;
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