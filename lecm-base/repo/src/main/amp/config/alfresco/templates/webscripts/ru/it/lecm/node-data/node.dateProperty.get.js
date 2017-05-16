(function () {
    var nodeRef = args["nodeRef"];
    var node = search.findNode(nodeRef);
    if (node) {
        model.result = utils.toISO8601(node.properties[args["property"]]);
    }
})();