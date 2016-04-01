function main() {
	var pointRef = args["pointRef"];
	var isExecuted = protocolService.checkPointExecutedStatus(pointRef);
	model.isExecuted = isExecuted;

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