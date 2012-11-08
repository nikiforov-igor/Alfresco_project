var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
var documentStateMachineBean = ctx.getBean("documentStateMachineBean");

var processes = documentStateMachineBean.getProcesses().toArray();

var machines = [];
for each (var process in processes) {
	machines.push({
		id: process,
		title: documentStateMachineBean.getTitle(process),
		description: documentStateMachineBean.getDescription(process)
	});
}

model.machines = machines;