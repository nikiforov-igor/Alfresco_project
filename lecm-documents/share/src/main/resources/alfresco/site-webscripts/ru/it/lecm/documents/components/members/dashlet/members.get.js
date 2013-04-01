<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main() {
    var hasPerm = hasPermission(args["nodeRef"], '_lecmPerm_MemberList');
    if(hasPerm){
        var url = "/lecm/document/api/getMembersFolder?nodeRef=" + args["nodeRef"];
        var json = remote.connect("alfresco").get(url);
        if (json.status == 200) {
            var obj = eval("(" + json + ")");
            model.folderRef = obj.nodeRef;
        }
    }
}

main();
