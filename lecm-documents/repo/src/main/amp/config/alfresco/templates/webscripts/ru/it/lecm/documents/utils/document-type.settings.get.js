var docType = args["docType"];
var node = "";
var draft = documentScript.getDraftRoot(docType);
if (draft) {
    node = draft.getNodeRef().toString();
}

var draftPath = documentScript.getDraftsPath();
var documentPath = documentScript.getDocumentsPath();

var archDirectories = statemachine.getArchiveFolders(docType);

model.nodeRef = node;
model.draftPath = draftPath;
model.documentPath = documentPath;
model.archivePath = archDirectories.join(",");
var filterObj = documentScript.getDefaultFilter(docType, (args["archive"] ? args["archive"] == "true" : false));
for (key in filterObj) {
    model.defaultKey = key;
    model.defaultFilter = filterObj[key];
}
