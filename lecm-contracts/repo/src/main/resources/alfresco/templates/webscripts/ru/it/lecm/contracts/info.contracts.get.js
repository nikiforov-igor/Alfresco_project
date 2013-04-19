var type = "lecm-contract:document";

var draftPath = contracts.getDraftPath();
var documentPath = documentScript.getDocumentsPath();
var paths = [draftPath, documentPath];

var map = documentScript.getFilters(type);
var list = [];
for (var key in map) {
    var amountContracts = contracts.getAmountContracts(paths, map[key].split(","));
    list.push({
        key: key,
        amountContracts: amountContracts,
        filter: map[key]
    });
}
model.list = list;

