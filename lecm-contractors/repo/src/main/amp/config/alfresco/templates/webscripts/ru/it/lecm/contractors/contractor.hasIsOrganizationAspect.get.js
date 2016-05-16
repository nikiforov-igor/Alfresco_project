var nodeRefs = args["nodeRef"].split(',');

model.result = {};

for each (var node in nodeRefs) {
    var nodeRef = search.findNode(node);
    if (nodeRef) {
        model.result[node] = nodeRef.hasAspect("lecm-orgstr-aspects:is-organization-aspect");
    }
}