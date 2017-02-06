<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">
function main() {
    AlfrescoUtil.param("nodeRef");
	var excludeType = AlfrescoUtil.param("excludeType", null);

	model.hasViewPerm = hasPermission(model.nodeRef, PERM_LINKS_VIEW);
	model.hasCreatePerm = hasPermission(model.nodeRef, PERM_LINKS_CREATE);
	model.hasDeletePerm = hasPermission(model.nodeRef, PERM_LINKS_DELETE);
	model.hasStatemachine = hasStatemachine(model.nodeRef);
	if (model.hasViewPerm) {
        var connections = getConnections(model.nodeRef, null, excludeType);
        if (connections) {
		    model.connections = connections;
        }
        var connectionsWithDocument = getConnectionsWithDocument(model.nodeRef, null, excludeType);
        if (connectionsWithDocument) {
		    model.connectionsWithDocument = connectionsWithDocument;
        }
	}
}

function getConnections(nodeRef, defaultValue, excludeType) {
	var url = '/lecm/document/connections/api/records?documentNodeRef=' + nodeRef + "&count=100&applyViewMode=true";
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

function getConnectionsWithDocument(nodeRef, defaultValue, excludeType) {
	var url = '/lecm/document/connections/api/getConnectionsWithDocument?documentNodeRef=' + nodeRef + "&applyViewMode=true";
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
