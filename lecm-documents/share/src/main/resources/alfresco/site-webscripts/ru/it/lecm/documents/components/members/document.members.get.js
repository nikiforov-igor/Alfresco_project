<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    var hasPerm = hasPermission(model.nodeRef);
    if (hasPerm) {
        model.members = getMembers(model.nodeRef);
    }
}

function hasPermission(nodeRef) {
    var url = '/lecm/security/api/getPermission?nodeRef=' + nodeRef + '&permission=lecmPerm_MemberList';
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        return false;
    }
    var permObj = eval('(' + result + ')');
    return (("" + permObj.hasPermission) ==  "true");
}

function getMembers(nodeRef) {
    var url = '/lecm/document/api/getMembers?nodeRef=' + nodeRef + "&skipCount=0&loadCount=5";
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        AlfrescoUtil.error(result.status, 'Could not get connections for node ' + nodeRef);
    }
    return eval('(' + result + ')');
}

main();

