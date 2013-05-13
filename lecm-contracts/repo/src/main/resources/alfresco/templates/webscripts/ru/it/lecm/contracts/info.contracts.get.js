var type = "lecm-contract:document";

var draftPath = contracts.getDraftPath();
var documentPath = documentScript.getDocumentsPath();

var paths = [draftPath, documentPath];

var archDirectories = statemachine.getArchiveFolders("lecm-contract:document");
for (var index in archDirectories) {
    var archName = archDirectories[index];
    var archFolder = companyhome.childByNamePath(archName);
    if (archFolder != null) {
        paths.push(archFolder.getQnamePath());
    }
}

var map = documentScript.getFilters(type);
var list = [];
var members = [];
for (var key in map) {
    var amountContracts = contracts.getAmountContracts(paths, map[key].split(","));
    list.push({
        key: key,
        amountContracts: amountContracts,
        filter: map[key]
    });
}
var amountMembers = contracts.getAmountMembers();
members.push({
    key:"participants",
    amountMembers:amountMembers
});
model.members=members;
model.list = list;