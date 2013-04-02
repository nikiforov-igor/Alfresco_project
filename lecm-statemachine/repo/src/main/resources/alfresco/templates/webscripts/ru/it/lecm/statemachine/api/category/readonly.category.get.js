var nodeRef = args['nodeRef'];
var category = args['category'];

if (nodeRef != null && category != null) {
    var node = search.findNode(nodeRef);
    model.result = statemachine.isReadOnlyCategory(node, category);
} else {
    model.result = true;
}
