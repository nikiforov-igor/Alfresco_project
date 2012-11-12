var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
var actionsBean = ctx.getBean("stateMachineActions");

var actions = [];
var actionsArray = actionsBean.getActionsByType("user").toArray();
for each (var action in actionsArray) {
	actions.push({
		id: action,
		title: actionsBean.getActionTitle(action)
	});
}
model.actions = actions;
