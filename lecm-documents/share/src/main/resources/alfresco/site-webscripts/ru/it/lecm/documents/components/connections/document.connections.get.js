<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    AlfrescoUtil.param("nodeRef");

    model.connections = getConnections(model.nodeRef);
}

function getConnections(nodeRef, defaultValue) {
    var url = '/lecm/connections/api/records?primaryDocumentNodeRef=' + nodeRef + "&skipItemsCount=0&loadItemsCount=5";
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
