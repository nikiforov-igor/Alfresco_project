var statemachineId = args["statemachineId"],
	typeDef;

if (statemachineId != null) {
	typeDef = base.getType(statemachineId.replace("_", ":"));
	model.title = typeDef.getTitle();
} else {
	status.setCode(500, "statemachineId is mandatory parameter!");
}