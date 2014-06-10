var nodeRef = args['nodeRef'];
var field = args['field'];

if (nodeRef != null && field != null) {
	var node = search.findNode(nodeRef);
	model.result = statemachine.isEditableField(node, field);
} else {
	model.result = false;
}
