var nodeRef = args["nodeRef"]
logger.log ("nodeRef is " + nodeRef);

var isActive = delegation.isDelegationActive (nodeRef);

model.isActive = isActive;