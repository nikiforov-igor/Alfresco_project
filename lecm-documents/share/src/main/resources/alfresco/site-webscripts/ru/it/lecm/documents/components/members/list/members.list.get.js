<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    model.members = getMembers(model.nodeRef);
    model.mayAdd = hasAddMemberPermission(model.nodeRef);
}

function getMembers(nodeRef) {
    var url = '/lecm/document/api/getMembers?nodeRef=' + nodeRef + "&skipCount=0&loadCount=1000";
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        AlfrescoUtil.error(result.status, 'Could not get connections for node ' + nodeRef);
    }
    return eval('(' + result + ')');
}

function hasAddMemberPermission(nodeRef) {
    var url = '/lecm/security/api/getPermission?nodeRef=' + nodeRef + '&permission=_lecmPerm_MemberAdd';
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        return false;
    }
    var permObj = eval('(' + result + ')');
    return (("" + permObj.hasPermission) ==  "true");
}


main();