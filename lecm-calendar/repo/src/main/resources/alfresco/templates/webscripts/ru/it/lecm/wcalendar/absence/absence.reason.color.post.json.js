var request = jsonUtils.toObject(json);

var nodeRef = request.nodeRef;
var node = search.findNode(nodeRef);
model.color =  node.properties["lecm-absence:color"];
