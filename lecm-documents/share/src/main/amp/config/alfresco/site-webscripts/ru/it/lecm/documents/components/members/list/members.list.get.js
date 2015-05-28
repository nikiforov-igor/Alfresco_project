<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    AlfrescoUtil.param("nodeRef");
    var members = getMembers(model.nodeRef);
    if (members != null) {
        model.members = members;
    }
    model.mayAdd = hasPermission(model.nodeRef, PERM_MEMBERS_ADD) && hasBusinessRole();

    model.hasStatemachine = hasStatemachine(model.nodeRef);

    model.mayDelete = user.isAdmin;
}

function getMembers(nodeRef) {
    var url = '/lecm/document/api/getMembers?nodeRef=' + nodeRef + "&skipCount=0&loadCount=1000";
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        return null;
    }
    return eval('(' + result + ')');
}

function hasBusinessRole() {
    var url = '/lecm/orgstructure/isCurrentEmployeeHasBusinessRole?roleId=BR_ADD_MEMBERS';
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        return false;
    }
    var res = eval('(' + result + ')');
    return (("" + res) ==  "true");
}
main();