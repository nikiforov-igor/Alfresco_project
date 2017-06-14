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
		var executorAssoc = node.assocs['lecm-protocol-ts:executor-assoc'];
		var isRegistrar = false;
		var isExecutor = false;
		if (registrar && registrar.length) {
			isRegistrar = orgstructure.getCurrentEmployee().equals(registrar[0]);
		}
		if (executorAssoc && executorAssoc.length) {
			isExecutor = orgstructure.getCurrentEmployee().equals(executorAssoc[0]);
		}
		model.isExecuted = !isRegistrar && !isExecutor;
	}
}

main();