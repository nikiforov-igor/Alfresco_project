function main() {
	var pointRef = json.get("pointRef");
	ordStatemachine.changePointStatus(pointRef,"EXECUTED_STATUS");
}

main();