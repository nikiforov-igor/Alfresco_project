var statemachineId = args["statemachineId"];

if (statemachineId != null && statemachineId != '') {

	var machinesFolder = companyhome.childByNamePath("statemachines");
	if (machinesFolder == null) {
		machinesFolder = companyhome.createNode("statemachines", "cm:folder", "cm:contains");
	}

	var documentsFolder = companyhome.childByNamePath("documents");
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
		var statemachineFolder = companyhome.childByNamePath("documents/" + statemachineId);
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
		var transitions = [];
		for each (var action in actionsNodes) {
			var actionId = action.properties["lecm-stmeditor:actionId"];
			var type = action.properties["lecm-stmeditor:actionExecution"];
			var actionChildren = action.getChildren();
			for each (var transition in actionChildren) {
				if (transition.assocs["lecm-stmeditor:transitionStatus"] != null) {
					var transitionStatus = transition.assocs["lecm-stmeditor:transitionStatus"][0];
					var transitionLabel = transition.properties["lecm-stmeditor:transitionLabel"];
					var userTransition = transitionLabel != null;
					if (!userTransition) {
						transitionLabel = transition.properties["lecm-stmeditor:transitionExpression"];
					}
					var transitionStatusLabel;
					if (transitionStatus.properties["lecm-stmeditor:endStatus"]) {
						transitionStatusLabel = "Завершено";
					} else {
						transitionStatusLabel = transitionStatus.properties["cm:name"];
					}
					transitions.push({
						user: userTransition,
						exp: transitionLabel,
						status: transitionStatusLabel
					});

				}
			}
		}
		statuses.push({
			name: status.properties["cm:name"] + (status.properties["lecm-stmeditor:startStatus"] ? " (S)" : ""),
			nodeRef: status.nodeRef.toString(),
			transitions: transitions,
			editable: "true"
		});
	}

	if (endStatus != null) {
		statuses.push({
			name: "Завершено",
			nodeRef: endStatus.nodeRef.toString(),
			transitions: [],
			editable: "false"
		});
	}

	model.statuses = statuses;
}
