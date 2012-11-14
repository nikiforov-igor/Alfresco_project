var statemachineId = args["statemachineId"];

if (statemachineId != null && statemachineId != '') {

	var machinesFolder = search.xpathSearch("/app:company_home/cm:statemachines")[0];
	if (machinesFolder == null) {
		machinesFolder = companyhome.createNode("statemachines", "cm:folder", "cm:contains");
	}

	var machine = null;
	var machines = machinesFolder.getChildren();
	for each (var m in machines) {
		if (m.properties["lecm-stmeditor:statemachineId"] == statemachineId) {
			machine = m;
			break;
		}
	}

	if (machine == null) {
		machine = machinesFolder.createNode(statemachineId, "lecm-stmeditor:statemachine", "cm:contains");
		machine.properties["lecm-stmeditor:statemachineId"] = statemachineId;
		machine.save();
	}

	var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
	var actionsBean = ctx.getBean("stateMachineActions");

	model.packageNodeRef = machine.nodeRef.toString();

	var machineStatuses = machine.getChildren();
	var statuses = [];
	for each (var status in machineStatuses) {
		var actionsNodes = status.getChildren();
		var actions = [];
		for each (var action in actionsNodes) {
			var actionId = action.properties["lecm-stmeditor:actionId"];
			actions.push({
				actionName: actionsBean.getActionTitle(actionId),
				actionId: actionId,
				nodeRef: action.nodeRef.toString(),
				transitions: []
			});
		}
		statuses.push({
			name: status.properties["cm:name"],
			nodeRef: status.nodeRef.toString(),
			actions: actions
		});
	}
	model.statuses = statuses;
}
