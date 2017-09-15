var documentNodeRef = args["documentNodeRef"];
var document = null;

if (documentNodeRef) {
    document = search.findNode(documentNodeRef);
}
var docTypeDictionary = null;
if (document) {
    var typeAssocName = "";
    if (document.typeShort == "lecm-contract:document"){
        typeAssocName ="lecm-contract:typeContract-assoc";
    } else {
        typeAssocName = "lecm-eds-document:document-type-assoc";
    }
    var typeAssoc =  document.assocs[typeAssocName];
    if (typeAssoc && typeAssoc.length > 0) {
        docTypeDictionary = typeAssoc[0];
    }
}
model.dictionary = docTypeDictionary;