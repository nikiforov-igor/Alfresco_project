var properties = json.get("properties").split(",");
var type = json.get("type");
var document = documentScript.createDocument(type, properties);
model.documentRef = document;