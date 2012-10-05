function main()
{
    var type = "";
    var nodeRef = "";
    if (args["nodeRef"] == null || args["nodeRef"] == "") {
    } else {
        nodeRef = search.findNode(args["nodeRef"]);
        type = nodeRef.properties["lecm-dic:type"];
    }
    model.type = type;
}
main();