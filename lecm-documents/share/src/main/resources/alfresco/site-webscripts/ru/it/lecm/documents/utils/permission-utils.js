function hasPermission(nodeRef, permission) {
    if (nodeRef == null || permission == null) {
        return false;
    }
    var url = '/lecm/security/api/getPermission?nodeRef=' + nodeRef + '&permission=' + permission;
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        return false;
    }
    var perm = eval('(' + result + ')');
    return (("" + perm) ==  "true");
}