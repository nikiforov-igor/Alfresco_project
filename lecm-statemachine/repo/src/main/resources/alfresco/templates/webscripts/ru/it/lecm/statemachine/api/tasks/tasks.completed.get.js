var nodeRef = args['nodeRef'];

var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
var stateMachineHelper = ctx.getBean("stateMachineHelper");

model.tasks = stateMachineHelper.getMyCompleteTasks(nodeRef);
model.count = model.tasks.size();
