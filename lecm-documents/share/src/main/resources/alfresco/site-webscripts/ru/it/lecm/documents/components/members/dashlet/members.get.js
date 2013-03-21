function main() {
    var url = "/lecm/document/api/getMembersFolder?nodeRef=" + args["nodeRef"];
    var json = remote.connect("alfresco").get(url);
    if (json.status == 200) {
        var obj = eval("(" + json + ")");
        model.folderRef = obj.nodeRef;
    }
}

main();
