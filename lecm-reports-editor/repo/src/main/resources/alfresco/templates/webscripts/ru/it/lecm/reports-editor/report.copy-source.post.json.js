function main() {
    var copyRef = json.get("copyRef");
    var copyToFile = json.get("copyToFile");
    var name = json.get("prop_cm_name");
    var code = json.get("prop_lecm-rpeditor_dataSourceCode");

    var fileNode = search.findNode(copyToFile);
    var node = search.findNode(copyRef);
    var result =
    {
        nodeRef: copyRef,
        success: false
    }

    if (fileNode.isContainer)
    {
        var copiedNode = node.copy(fileNode, true);
        result.nodeRef = copiedNode.nodeRef.toString();
        if (copiedNode) {
            copiedNode.properties["cm:name"] = name;
            copiedNode.properties["lecm-rpeditor:dataSourceCode"] = code;
            copiedNode.save();
        }
    }
    result.success = (result.nodeRef !== null);


    model.result = result;

}
main();