var nodeRef = args['nodeRef'];
var loadCount = args['loadCount'];
if (loadCount == null) {
    loadCount = 0;
}

var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
var stateMachineHelper = ctx.getBean("stateMachineHelper");

model.data = stateMachineHelper.getMyActiveTasks(nodeRef, loadCount);