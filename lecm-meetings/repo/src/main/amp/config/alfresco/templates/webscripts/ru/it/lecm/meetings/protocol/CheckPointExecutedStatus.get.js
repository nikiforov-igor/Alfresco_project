function main() {
	var pointRef = args["pointRef"];
	var isExecuted = protocolService.checkPointExecutedStatus(pointRef);
	var isRemoved = protocolService.checkPointRemovedStatus(pointRef);
	if (isExecuted == true || isRemoved == true) {
		model.isExecuted = true;
	}
	else {
		model.isExecuted = false;
	}

	if ("true" !== isExecuted) {
		var node = search.findNode(pointRef);
		var protocol = node.parent.parent.parent;
		var registrar = protocol.assocs['lecm-document-aspects:registrator-assoc'];

		if (registrar && registrar.length) {
			model.isExecuted = !orgstructure.getCurrentEmployee().equals(registrar[0]);
		}
	}
}

main();