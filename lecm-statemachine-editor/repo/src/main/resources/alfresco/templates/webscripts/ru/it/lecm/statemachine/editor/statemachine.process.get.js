var statemachineId = args["statemachineId"];

if (statemachineId != null && statemachineId != '') {

	var machinesFolder = lecmRepository.getHomeRef().childByNamePath("statemachines");
	if (machinesFolder == null) {
		machinesFolder = lecmRepository.getHomeRef().createNode("statemachines", "cm:folder", "cm:contains");
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
		var startStatus = statuses.createNode("Черновик", "lecm-stmeditor:taskStatus", "cm:contains");
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
		var actionsNodes = status.childByNamePath("actions").getChildren();
		var transitions = [];
		for each (var action in actionsNodes) {
			var actionId = action.properties["lecm-stmeditor:actionId"];
			var type = action.properties["lecm-stmeditor:actionExecution"];
			var actionChildren = action.getChildren();
			for each (var transition in actionChildren) {
				if (transition.assocs["lecm-stmeditor:transitionStatus"] != null) {
					var transitionStatus = transition.assocs["lecm-stmeditor:transitionStatus"][0];
					var transitionLabel = transition.properties["lecm-stmeditor:transitionLabel"];
					var transitionWorkflow = transition.properties["lecm-stmeditor:workflowId"];
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

                    var transitionTypeLabel = "";
                    if (actionId == "WaitForDocumentChange") {
                        transitionTypeLabel = "По изменению документа";
                    } else if (actionId == "FinishStateWithTransition" && transitionWorkflow != null) {
                        transitionTypeLabel = "Переход с запуском процесса"
                    } else if (actionId == "FinishStateWithTransition") {
                        transitionTypeLabel = "Переход выполняемый пользователем"
                    } else if (actionId == "TransitionAction") {
                        transitionTypeLabel = "Переход по завершению ранее запущенного процесса"
                    } else {
                        transitionTypeLabel = actionId;
                    }
					transitions.push({
						user: userTransition,
						exp: transitionLabel,
						status: transitionStatusLabel,
                        label: transitionTypeLabel
					});

				}
			}
		}
        if (status.assocs["lecm-stmeditor:transitionStatus"] != null) {

            var timeoutStatusLabel = status.assocs["lecm-stmeditor:transitionStatus"][0].properties["cm:name"]
            var duration = status.properties["lecm-stmeditor:timerDuration"];
            transitions.push({
                user: false,
                exp: "Через " + duration + " р.д.",
                status: timeoutStatusLabel,
                label: "По таймауту"
            });
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
