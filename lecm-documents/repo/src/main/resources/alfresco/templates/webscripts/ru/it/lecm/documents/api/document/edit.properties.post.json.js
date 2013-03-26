var nodeRef = json.get("nodeRef");
var properties = json.get("properties").split(",");
var document = documentScript.editDocument(nodeRef, properties);
model.documentRef = document;