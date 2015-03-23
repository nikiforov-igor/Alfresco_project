function main (){
	var documentRef = json.get("documentRef");

	var document = search.findNode(documentRef);
	var statemachineVer = statemachine.getStatemachineVersion(document);
	var statemachineId = statemachine.getStatemachineId(document);
	var dbId = document.properties['sys:node-dbid'];

	model.statemachineVer = statemachineVer;
	model.statemachineId = statemachineId;
	model.dbId = dbId;
}

main ();