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

	model.packageNodeRef = machine.nodeRef.toString();

	var machineStatuses = machine.getChildren();
	var statuses = [];
	for each (var status in machineStatuses) {
		statuses.push({
			name: status.properties["cm:name"],
			actions: []
		});
	}
	model.statuses = statuses;
}
