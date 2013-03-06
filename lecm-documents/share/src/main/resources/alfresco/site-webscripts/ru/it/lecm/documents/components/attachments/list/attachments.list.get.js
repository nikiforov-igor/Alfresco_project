<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    AlfrescoUtil.param("nodeRef");

    model.rootFolder = getRootFolders(model.nodeRef);
}

function getRootFolders(nodeRef, defaultValue) {
    var url = '/lecm/document/attachments/api/folders?documentNodeRef=' + nodeRef;
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