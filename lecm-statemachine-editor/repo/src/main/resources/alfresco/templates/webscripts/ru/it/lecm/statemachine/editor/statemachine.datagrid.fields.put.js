var nodeRef = args["nodeRef"];
var value = args["value"];
var node = search.findNode(nodeRef);
node.properties["lecm-stmeditor:editableField"] = value;
node.save();
