//statusNodeRef
//actionId

var statusNodeRef = args["statusNodeRef"];
var actionId = args["actionId"];

var statusNode = search.findNode(statusNodeRef);
var actionNode = statusNode.createNode(null, "lecm-stmeditor:" + actionId, "cm:contains");
actionNode.properties["lecm-stmeditor:actionId"] = actionId;
actionNode.save();

model.actionNodeRef = actionNode.nodeRef.toString();
