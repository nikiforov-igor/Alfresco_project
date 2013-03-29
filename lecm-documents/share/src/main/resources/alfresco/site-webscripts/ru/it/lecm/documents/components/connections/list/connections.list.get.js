<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    AlfrescoUtil.param("nodeRef");

	model.hasViewPerm = hasViewConnectionsPermission(model.nodeRef);
	model.hasCreatePerm = hasCreateConnectionsPermission(model.nodeRef);
	model.hasDeletePerm = hasDeleteConnectionsPermission(model.nodeRef);
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

function hasViewConnectionsPermission(nodeRef) {
	var url = '/lecm/security/api/getPermission?nodeRef=' + nodeRef + '&permission=_lecmPerm_LinksView';
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		return false;
	}
	var permObj = eval('(' + result + ')');
	return (("" + permObj.hasPermission) ==  "true");
}

function hasCreateConnectionsPermission(nodeRef) {
	var url = '/lecm/security/api/getPermission?nodeRef=' + nodeRef + '&permission=_lecmPerm_LinksCreate';
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		return false;
	}
	var permObj = eval('(' + result + ')');
	return (("" + permObj.hasPermission) ==  "true");
}

function hasDeleteConnectionsPermission(nodeRef) {
	var url = '/lecm/security/api/getPermission?nodeRef=' + nodeRef + '&permission=_lecmPerm_LinksDelete';
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		return false;
	}
	var permObj = eval('(' + result + ')');
	return (("" + permObj.hasPermission) ==  "true");
}

main();