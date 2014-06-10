
var draftPath = documentScript.getDraftsPath();
var documentPath = documentScript.getDocumentsPath();

var paths = [];

if (draftPath) {
    paths.push(draftPath);
}

if (documentPath) {
    paths.push(documentPath);
}

var archDirectories = statemachine.getArchiveFolders("lecm-additional-document:additionalDocument");
for (var index in archDirectories) {
    paths.push(archDirectories[index]);
}
model.docs = contracts.getAdditionalDocsByType(paths, args.type, args.considerFilter, ("" + args.active == "true"));