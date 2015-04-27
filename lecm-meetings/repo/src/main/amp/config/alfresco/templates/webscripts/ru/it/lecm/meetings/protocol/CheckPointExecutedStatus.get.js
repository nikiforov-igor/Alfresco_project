function main() {
	var pointRef = args["pointRef"];
	var isExecuted = protocolService.checkPointExecutedStatus(pointRef);
	model.isExecuted = isExecuted;
}

main();