function main() {
    var storeStr = remote.connect("alfresco").get("/lecm/search-queries/getRoot");
    if (storeStr.status == 200) {
        var root = eval("(" + storeStr + ")");
        model.storeRoot =  root.nodeRef;
    }
}

main();
