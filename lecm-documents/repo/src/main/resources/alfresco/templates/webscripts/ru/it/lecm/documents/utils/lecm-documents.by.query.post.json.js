var skipCount = json.has("skipCount") ? parseInt(json.get("skipCount")) : 0;
var loadCount = json.has("loadCount") ? parseInt(json.get("loadCount")) : -1;

var documents = documentScript.getDocumentsByQuery(json.get("query"), skipCount, loadCount);

var items = [];

if (documents != null) {
    for (var i = 0; i < documents.length; i++) {
        items.push({
            nodeRef: documents[i].getNodeRef().toString(),
            presentString: documents[i].properties["lecm-document:present-string"],
            extPresentString: documents[i].properties["lecm-document:ext-present-string"]
        })
    }
}
model.items = items;