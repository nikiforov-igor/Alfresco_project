var type = args["docType"];

var draftPath = documentScript.getDraftPath(args["draftRoot"]);
var documentPath = documentScript.getDocumentsPath();

var paths = [draftPath, documentPath];

var archDirectories = statemachine.getArchiveFolders(type);
for (var index in archDirectories) {
    paths.push(archDirectories[index]);
}

var map = documentScript.getFilters(type);

var types = [];
types.push(type);

var list = [];
var members = [];

for (var key in map) {
    var amountDocs = documentScript.getAmountDocuments(types, paths, map[key].split(","));
    list.push({
        key: key,
        skip: args["skippedStatuses"] != null && args["skippedStatuses"].indexOf(key) >= 0,
        amount: amountDocs,
        filter: map[key]
    });
}
var amountMembers = documentScript.getAmountMembers(type);
members.push({
    key:"Участники",
    amountMembers:amountMembers
});
model.members=members;
model.list = list;