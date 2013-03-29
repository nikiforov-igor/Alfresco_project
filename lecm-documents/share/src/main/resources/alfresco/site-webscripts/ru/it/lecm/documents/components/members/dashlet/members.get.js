function main() {
    var hasPerm = hasViewMembersPermission(args["nodeRef"]);
    if(hasPerm){
        var url = "/lecm/document/api/getMembersFolder?nodeRef=" + args["nodeRef"];
        var json = remote.connect("alfresco").get(url);
        if (json.status == 200) {
            var obj = eval("(" + json + ")");
            model.folderRef = obj.nodeRef;
        }
    }
}

function hasViewMembersPermission(nodeRef) {
    var url = '/lecm/security/api/getPermission?nodeRef=' + nodeRef + '&permission=_lecmPerm_MemberList';
    var result = remote.connect("alfresco").get(url);
    if (result.status != 200) {
        return false;
    }
    var permObj = eval('(' + result + ')');
    return (("" + permObj.hasPermission) ==  "true");
}

main();
