var node = contracts.getDraftRoot().getNodeRef().toString();
var draftPath = contracts.getDraftPath();
var documentPath = documentScript.getDocumentsPath();

var archDirectories = statemachine.getArchiveFolders("lecm-contract:document");

var archivePaths = [];
for (var index in archDirectories) {
    var archName = archDirectories[index];
    var archFolder = companyhome.childByNamePath(archName);
    if (archFolder != null) {
        archivePaths.push(archFolder.getQnamePath());
    }
}

model.nodeRef = node;
model.draftPath = draftPath;
model.documentPath = documentPath;
model.archivePath = archivePaths.join(",");