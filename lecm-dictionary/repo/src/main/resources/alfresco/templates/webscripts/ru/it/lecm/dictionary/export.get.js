function main()
{
    var type = "";
    var nodeRef = "";
    if (args["nodeRef"] == null || args["nodeRef"] == "") {
    } else {
        nodeRef = search.findNode(args["nodeRef"]);
        type = exportXML.getXMLFile(args["nodeRef"]);
    }
    model.type = type;
}
main();