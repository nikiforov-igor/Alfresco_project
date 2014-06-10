var nodeRef = args["nodeRef"]
logger.log ("nodeRef is " + nodeRef);

var procuracies = null;
var isSubjectValid;

if (!nodeRef) {
	nodeRef = person.nodeRef;
}
procuracies = delegation.actualizeProcuracies (nodeRef);
if (procuracies) {
	model.procuracies = procuracies;
} else {
	model.procuracies = [];
}
