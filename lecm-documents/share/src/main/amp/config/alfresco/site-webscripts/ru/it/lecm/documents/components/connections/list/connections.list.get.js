<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">
function main() {
    AlfrescoUtil.param("nodeRef");

	model.hasViewPerm = hasPermission(model.nodeRef, PERM_LINKS_VIEW);
	model.hasCreatePerm = hasPermission(model.nodeRef, PERM_LINKS_CREATE);
	model.hasDeletePerm = hasPermission(model.nodeRef, PERM_LINKS_DELETE);
	model.hasStatemachine = hasStatemachine(model.nodeRef);
	if (model.hasViewPerm) {
        var connections = getConnections(model.nodeRef);
        if (connections != null) {
		    model.connections = connections;
        }
        var connectionsWithDocument = getConnectionsWithDocument(model.nodeRef);
        if (connectionsWithDocument != null) {
		    model.connectionsWithDocument = connectionsWithDocument;
        }
	}
}

function getConnections(nodeRef, defaultValue) {
	var url = '/lecm/document/connections/api/records?documentNodeRef=' + nodeRef + "&count=100&applyViewMode=true";
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		if (defaultValue !== undefined) {
			return defaultValue;
		}
		return null;
	}
	return eval('(' + result + ')');
}

function getConnectionsWithDocument(nodeRef, defaultValue) {
	var url = '/lecm/document/connections/api/getConnectionsWithDocument?documentNodeRef=' + nodeRef + "&applyViewMode=true";
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