var action = url.templateArgs["action"];
var nodeRef = args["nodeRef"];

logger.log ("action is " + action);
logger.log ("nodeRef is " + nodeRef);

function startDelegation () {
	logger.log ("start delegation");
	delegation.startDelegation (nodeRef);
	model.message = "start delegation";
}

function stopDelegation () {
	logger.log ("stop delegation");
	delegation.stopDelegation (nodeRef);
	model.message = "stop delegation";
}

switch (action) {
	case "start":
		startDelegation ();
		break;
	case "stop":
		stopDelegation ();
		break;
	default:
		status.code = 400;
		status.message = '"' + action + '" specified in the Request-Line is not allowed for the resource identified by the Request-URI. Allowed values are "start" or "stop"';
		status.redirect = true;
		break;
}
