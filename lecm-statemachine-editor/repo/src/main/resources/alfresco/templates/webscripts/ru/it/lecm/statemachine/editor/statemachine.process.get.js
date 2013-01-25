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
		var roles = machine.createNode("roles", "lecm-stmeditor:roles", "cm:contains")
		machine.properties["lecm-stmeditor:rolesFolder"] = roles.nodeRef.toString();
		machine.save();

		var statuses = machine.createNode("statuses", "lecm-stmeditor:statuses", "cm:contains")
		var startStatus = statuses.createNode("Start", "lecm-stmeditor:status", "cm:contains");
		startStatus.properties["lecm-stmeditor:forDraft"] = true;
		startStatus.properties["lecm-stmeditor:startStatus"] = true;
		startStatus.save();
	}

	var statuses = machine.childByNamePath("statuses");
	model.machineNodeRef = machine.nodeRef.toString();
	model.packageNodeRef = statuses.nodeRef.toString();

	var machineStatuses = statuses.getChildren();
	var statuses = [];
	var endStatus = null;
	for each (var status in machineStatuses) {
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
			isStarted: status.properties["lecm-stmeditor:startStatus"] ? "true" : "false",
			forDraft: status.properties["lecm-stmeditor:forDraft"] ? "true" : "false"
		});
	}

	if (endStatus != null) {
		statuses.push({
			name: "Завершено",
			nodeRef: endStatus.nodeRef.toString(),
			transitions: [],
			isStarted: "false",
			forDraft: "false"
		});
	}

	model.statuses = statuses;
}
