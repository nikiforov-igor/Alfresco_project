var node = contracts.getDraftRoot().getNodeRef().toString();
var draftPath = contracts.getDraftPath();
var documentPath = documentScript.getDocumentsPath();

model.nodeRef = node;
model.draftPath = draftPath;
model.documentPath = documentPath;