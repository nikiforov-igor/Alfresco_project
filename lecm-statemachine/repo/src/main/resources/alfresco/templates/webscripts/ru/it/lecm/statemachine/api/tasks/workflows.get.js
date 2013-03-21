var nodeRef = args['nodeRef'];
var state = args['state'];
if (state == null) {
    state = "";
}

var activeWorkflowsLimit = args['activeWorkflowsLimit'];
if (activeWorkflowsLimit == null) {
    activeWorkflowsLimit = 0;
}

var node = search.findNode(nodeRef);

model.data = statemachine.getWorkflows(node, state, activeWorkflowsLimit);
