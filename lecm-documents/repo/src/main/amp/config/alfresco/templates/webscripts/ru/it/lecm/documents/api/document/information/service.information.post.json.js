function main (){
	var documentRef = json.get("documentRef");

	var document = search.findNode(documentRef);
	var statemachineVer = statemachine.getStatemachineVersion(document);
	var statemachineId = statemachine.getStatemachineId(document);

	model.statemachineVer = statemachineVer;
	model.statemachineId = statemachineId;
}

main ();