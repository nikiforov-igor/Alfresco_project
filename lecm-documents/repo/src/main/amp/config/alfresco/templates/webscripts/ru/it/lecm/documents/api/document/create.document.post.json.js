var properties = json.get("properties").split(",");
var associations = json.get("associations").split(",");
var type = json.get("type");
var document = documentScript.createDocument(type, properties,associations);
model.documentRef = document;