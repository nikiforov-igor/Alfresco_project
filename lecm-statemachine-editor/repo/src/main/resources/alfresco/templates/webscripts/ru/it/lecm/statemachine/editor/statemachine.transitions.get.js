var actionNodeRef = args["actionNodeRef"];

var action = search.findNode(actionNodeRef);

var transitions = [];

var assocs = action.childAssocs["lecm-stmeditor:transitions"];
for each (var transition in assocs) {
	transitions.push({
		expression: transition.properties["lecm-stmeditor:transitionExpression"],
		transition: transition.properties["lecm-stmeditor:transitionStatus"]
	});
}
model.transitions = transitions;
