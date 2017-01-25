var statemachineId = args["statemachineId"];

if (statemachineId != null && statemachineId != '') {

	var machinesFolder = lecmRepository.getHomeRef().childByNamePath("statemachines");
	if (machinesFolder == null) {
		machinesFolder = lecmRepository.getHomeRef().createNode("statemachines", "cm:folder", "cm:contains");
        var typesFolder = machinesFolder.createNode("types", "cm:folder", "cm:contains");
        typesFolder.createNode(null, "lecm-stmeditor:userTransition", "cm:contains");
        typesFolder.createNode(null, "lecm-stmeditor:transitionWorkflow", "cm:contains");
        typesFolder.createNode(null, "lecm-stmeditor:transitionForm", "cm:contains");
        typesFolder.createNode(null, "lecm-stmeditor:transitionFormTrans", "cm:contains");
        typesFolder.createNode(null, "lecm-stmeditor:UserWorkflowEntity", "cm:contains");
        typesFolder.createNode(null, "lecm-stmeditor:StartWorkflowEntity", "cm:contains");
        typesFolder.createNode(null, "lecm-stmeditor:ScriptActionEntity", "cm:contains");
	}

    machinesFolder.setInheritsPermissions(false);

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

        var statuses = machine.createNode("statuses", "lecm-stmeditor:statuses", "cm:contains")
		var startStatus = statuses.createNode("Черновик", "lecm-stmeditor:taskStatus", "cm:contains");
		startStatus.properties["lecm-stmeditor:forDraft"] = true;
		startStatus.properties["lecm-stmeditor:startStatus"] = true;
		startStatus.save();
	}

    //Создание папки с ролями
    var rolesList = machine.childByNamePath("roles-list");
    if (rolesList == null) {
        var roles = machine.createNode("roles-list", "cm:folder", "cm:contains")
        machine.properties["lecm-stmeditor:staticRolesList"] = roles.nodeRef.toString();
        machine.properties["lecm-stmeditor:dynamicRolesList"] = roles.nodeRef.toString();
        machine.save();
    }

    //Создание папки с версиями
    var versions = machinesFolder.childByNamePath("versions");
    if (versions == null) {
        versions = machinesFolder.createNode("versions", "cm:folder", "cm:contains")
    }
    var version = versions.childByNamePath(statemachineId);
    if (version == null) {
        version = versions.createNode(statemachineId, "lecm-stmeditor:versions", "cm:contains");
        version.properties["lecm-stmeditor:last_version"] = 0;
        version.save();
    }

    //Создание папки с альтернативным началом
    var alternatives = machine.childByNamePath("alternatives");
    if (alternatives == null) {
        alternatives = machine.createNode("alternatives", "cm:folder", "cm:contains")
        machine.properties["lecm-stmeditor:alternativesFolder"] = alternatives.nodeRef.toString();
        machine.save();
    }

    var statuses = machine.childByNamePath("statuses");
	model.machineNodeRef = machine.nodeRef.toString();
	model.packageNodeRef = statuses.nodeRef.toString();
	model.versionsNodeRef = version.nodeRef.toString();
    var isSimple = machine.properties["lecm-stmeditor:simple-document"];
	model.isSimple = isSimple != null && isSimple;

	var aspectQName = base.createQName('lecm-document-aspects:finalize-to-unit');
	var aspects = base.getType(statemachineId.replace("_", ":")).getDefaultAspectNames();
    model.isFinalizeToUnit = aspects.contains(aspectQName);


	var machineStatuses = statuses.childAssocs["contains"];
	var statuses = [];
	var endStatus = null;

    var startTransitions = [];

    var alternative = alternatives.getChildren();
    for (var i = 0; i < alternative.length; i++) {
        startTransitions.push({
            user: "false",
            exp: alternative[i].properties["lecm-stmeditor:alternativeExpression"],
            status: alternative[i].assocs["lecm-stmeditor:alternativeStatus"] != null ? alternative[i].assocs["lecm-stmeditor:alternativeStatus"][0].properties["cm:name"] : "",
            label: msg.get("label.automatic_transition")
        });
    }

    startTransitions.push({
        user: "false",
        exp: msg.get("label.default"),
        status: machineStatuses[0].properties["cm:name"],
        label: msg.get("label.automatic_transition")
    });

    statuses.push({
        name: "Начало",
        nodeRef: "",
        transitions: startTransitions,
        type: "start",
        forDraft: "false"
    });

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
					var transitionStatusLabel = "";
					if (transitionStatus.properties["lecm-stmeditor:endStatus"]) {
						transitionStatusLabel = "Завершено";
					} else {
						transitionStatusLabel = transitionStatus.properties["cm:name"];
					}

                    var transitionTypeLabel = "";
                    if (actionId == "WaitForDocumentChange") {
                        transitionTypeLabel = msg.get("label.on_document_change");
                    } else if (actionId == "FinishStateWithTransition" && transitionWorkflow != null) {
                        transitionTypeLabel = msg.get("label.transition_with_srart_workflow")
                    } else if (actionId == "FinishStateWithTransition") {
                        transitionTypeLabel = msg.get("label.user_transition")
                    } else if (actionId == "TransitionAction") {
                        transitionTypeLabel = msg.get("label.transition_on_workflow_end")
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
                exp: msg.get('label.timer_duration_exp').replace('{0}', duration),
                status: timeoutStatusLabel,
                label: msg.get("label.after_timeout")
            });
        }
		statuses.push({
			name: status.properties["cm:name"] + (status.properties["lecm-stmeditor:startStatus"] ? " (S)" : ""),
			nodeRef: status.nodeRef.toString(),
			transitions: transitions,
			type: status.properties["lecm-stmeditor:startStatus"] ? "default" : "normal",
			forDraft: status.properties["lecm-stmeditor:forDraft"] ? "true" : "false"
		});
	}

	if (endStatus != null) {
		statuses.push({
			name: "Завершено",
			nodeRef: endStatus.nodeRef.toString(),
			transitions: [],
			type: "normal",
			forDraft: "false"
		});
	}

	model.statuses = statuses;
}
