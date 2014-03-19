function main() {
	var pointRef = args["pointRef"];
	var isExecuted = ordStatemachine.checkPointExecutedStatus(pointRef);
	model.isExecuted = isExecuted;
}

main();