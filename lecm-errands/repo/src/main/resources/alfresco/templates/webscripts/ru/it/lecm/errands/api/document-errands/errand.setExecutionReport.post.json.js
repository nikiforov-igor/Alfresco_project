function main() {
	var nodeRef = json.get("nodeRef");
	var executionReport = json.get("executionReport");

	errands.setExecutionReport(nodeRef, executionReport);
	model.success = true;
}
main();