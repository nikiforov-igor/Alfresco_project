var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
var actionsBean = ctx.getBean("stateMachineActions");

function getActions(type, execution) {
	var actions = [];
	var actionsArray = actionsBean.getActions(type, execution).toArray();
	for each (var action in actionsArray) {
		actions.push({
			id: action,
			title: actionsBean.getActionTitle(action)
		});
	}
	return actions;
}

model.startActions = getActions("user", "start");
model.takeActions = getActions("user", "take");
model.endActions = getActions("user", "end");


