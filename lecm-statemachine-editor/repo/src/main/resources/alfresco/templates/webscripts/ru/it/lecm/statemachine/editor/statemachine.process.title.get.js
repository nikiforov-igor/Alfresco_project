var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
var documentStateMachineBean = ctx.getBean("documentStateMachineBean");

var processId = args["statemachineId"];
model.title = documentStateMachineBean.getTitle(processId);
