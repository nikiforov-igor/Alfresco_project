var type = "lecm-contract:document";

var draftPath = contracts.getDraftPath();
var documentPath = documentScript.getDocumentsPath();
var paths = [draftPath, documentPath];

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
var amountMembers = contracts.getAmountMembers(paths, map[0].split(","));
members.push({
    key:"participants",
    amountMembers:amountMembers
});
model.members=members;
model.list = list;