var node = contracts.getDraftRoot().getNodeRef().toString();
var draftPath = contracts.getDraftPath();
var documentPath = documentScript.getDocumentsPath();

var archDirectories = statemachine.getArchiveFolders("lecm-contract:document");

model.nodeRef = node;
model.draftPath = draftPath;
model.documentPath = documentPath;
model.archivePath = archDirectories.join(",");
var filterObj = documentScript.getDefaultFilter("lecm-contract:document", false);
for (key in filterObj) {
    model.defaultKey = key;
    model.defaultFilter = filterObj[key];
}
