var statemachineId = args["statemachineId"];

if (statemachineId != null && statemachineId != '') {

	var machinesFolder = search.xpathSearch("/app:company_home/cm:statemachines")[0];
	if (machinesFolder == null) {
		machinesFolder = companyhome.createNode("statemachines", "cm:folder", "cm:contains");
	}

	var documentsFolder = search.xpathSearch("/app:company_home/cm:documents")[0];
	if (documentsFolder == null) {
		documentsFolder = companyhome.createNode("documents", "cm:folder", "cm:contains");
	}

	var machine = null;
	var machines = machinesFolder.getChildren();
	for each (var m in machines) {
		if (m.properties["cm:name"] == statemachineId) {
			machine = m;
			break;
		}
	}

	if (machine == null) {
		machine = machinesFolder.createNode(statemachineId, "lecm-stmeditor:statemachine", "cm:contains");
		machine.properties["lecm-stmeditor:machineFolder"] = statemachineId;
		machine.save();
		var statemachineFolder = search.xpathSearch("/app:company_home/cm:documents/cm:" + statemachineId)[0];
		if (statemachineFolder == null) {
			documentsFolder.createNode(statemachineId, "cm:folder", "cm:contains");
		}
	}

	var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
	var actionsBean = ctx.getBean("stateMachineActions");

	model.packageNodeRef = machine.nodeRef.toString();

	var machineStatuses = machine.getChildren();
	var statuses = [];
	for each (var status in machineStatuses) {
		var actionsNodes = status.getChildren();
		var startActions = [];
		var takeActions = [];
		var endActions = [];
		for each (var action in actionsNodes) {
			var actionId = action.properties["lecm-stmeditor:actionId"];
			var type = action.properties["lecm-stmeditor:actionExecution"];
			var actionDescriptor  ={
					actionName: actionsBean.getActionTitle(actionId),
					actionId: actionId,
					nodeRef: action.nodeRef.toString(),
					transitions: []
				};
			if (type == "start") {
				startActions.push(actionDescriptor);
			} else if (type == "take") {
				takeActions.push(actionDescriptor);
			} else if (type == "end") {
				endActions.push(actionDescriptor);
			}
		}
		statuses.push({
			name: status.properties["cm:name"],
			nodeRef: status.nodeRef.toString(),
			startActions: startActions,
			takeActions: takeActions,
			endActions: endActions
		});
	}
	model.statuses = statuses;
}
