var type = args["type"];
var draftPath = contracts.getDraftPath();
var documentPath = documentScript.getDocumentsPath();
var path = [draftPath,documentPath];
var map = documentScript.getFilters(type);
var list = [];
for (var key in map) {
    var amountContracts = contracts.getAmountContracts("lecm-contract:document",path,map[key].split(","));
    list.push({
        key:key,
        amountContracts:amountContracts,
        filter:map[key]
    });
}
model.list=list