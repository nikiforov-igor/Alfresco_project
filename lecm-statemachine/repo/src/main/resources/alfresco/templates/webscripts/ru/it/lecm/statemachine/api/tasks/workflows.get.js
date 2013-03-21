var nodeRef = args['nodeRef'];
var state = args['state'];
if (state == null) {
    state = "";
}

var activeWorkflowsLimit = args['activeWorkflowsLimit'];
if (activeWorkflowsLimit == null) {
    activeWorkflowsLimit = 0;
}

var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
var stateMachineHelper = ctx.getBean("stateMachineHelper");

model.data = stateMachineHelper.getWorkflows(nodeRef, state, activeWorkflowsLimit);