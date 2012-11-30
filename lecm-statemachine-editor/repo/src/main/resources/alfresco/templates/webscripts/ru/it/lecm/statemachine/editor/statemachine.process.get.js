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
		var statemachineFolder = search.xpathSearch("/app:company_home/cm:documents/cm:" + statemachineId)[0];
		if (statemachineFolder == null) {
			var machineDocumentsFolder = documentsFolder.createNode(statemachineId, "cm:folder", "cm:contains");
			machine.properties["lecm-stmeditor:documentsFolder"] = machineDocumentsFolder;
		}
		machine.save();

		var endStatus = machine.createNode("END", "lecm-stmeditor:status", "cm:contains")
		endStatus.properties["lecm-stmeditor:endStatus"] = true;
		endStatus.save();
	}

	var ctx = Packages.org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext();
	var actionsBean = ctx.getBean("stateMachineActions");

	model.packageNodeRef = machine.nodeRef.toString();

	var machineStatuses = machine.getChildren();
	var statuses = [];
	var endStatus = null;
	for each (var status in machineStatuses) {
		if (status.properties["lecm-stmeditor:endStatus"]) {
			endStatus = status;
			continue;
		}
		var actionsNodes = status.getChildren();
		var startActions = [];
		var userActions = [];
		var transitionActions = [];
		var endActions = [];
		for each (var action in actionsNodes) {
			var actionId = action.properties["lecm-stmeditor:actionId"];
			var type = action.properties["lecm-stmeditor:actionExecution"];
			var transitions = [];
			var actionChildren = action.getChildren();
			for each (var transition in actionChildren) {
				if (transition.assocs["lecm-stmeditor:transitionStatus"] != null) {
					var transitionStatus = transition.assocs["lecm-stmeditor:transitionStatus"][0];
					if (transitionStatus.properties["lecm-stmeditor:endStatus"]) {
						transitions.push("Завершено");
					} else {
						transitions.push(transitionStatus.properties["cm:name"]);
					}
				}
			}
			var actionDescriptor = {
					actionName: actionsBean.getActionTitle(actionId),
					actionId: actionId,
					nodeRef: action.nodeRef.toString(),
					transitions: transitions
				};
			if (type == "start") {
				startActions.push(actionDescriptor);
			} else if (type == "user") {
				userActions.push(actionDescriptor);
			} else if (type == "transition") {
				transitionActions.push(actionDescriptor);
			} else if (type == "end") {
				endActions.push(actionDescriptor);
			}

		}
		statuses.push({
			name: status.properties["cm:name"] + (status.properties["lecm-stmeditor:startStatus"] ? " (S)" : ""),
			nodeRef: status.nodeRef.toString(),
			startActions: startActions,
			userActions: userActions,
			transitionActions: transitionActions,
			endActions: endActions,
			editable: "true"
		});
	}

	if (endStatus != null) {
		statuses.push({
			name: "Завершено",
			nodeRef: endStatus.nodeRef.toString(),
			startActions: [],
			userActions: [],
			transitionActions: [],
			endActions: [],
			editable: "false"
		});
	}

	model.statuses = statuses;
}
