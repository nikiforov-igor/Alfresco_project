(function() {
    var nodeRef = args["nodeRef"];
    var node = search.findNode(nodeRef);
    model.result = node.typeShort;
}
)();