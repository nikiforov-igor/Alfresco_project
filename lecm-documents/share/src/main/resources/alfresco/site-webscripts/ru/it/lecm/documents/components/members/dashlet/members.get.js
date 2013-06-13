<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    var hasPerm = hasPermission(model.nodeRef, PERM_MEMBERS_LIST);

    if (hasPerm) {
        model.members = getMembers(model.nodeRef);
    }
}

function getMembers(nodeRef) {
    var url = '/lecm/document/api/getMembers?nodeRef=' + nodeRef + "&skipCount=0&loadCount=5";
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        AlfrescoUtil.error(result.status, 'Could not get members for node ' + nodeRef);
    }
    return eval('(' + result + ')');
}

main();
