var skipCount = args["skipCount"] != null ? parseInt(args["skipCount"]) : 0;
var loadCount = args["loadCount"] != null ? parseInt(args["loadCount"]) : -1;

var documents = documentScript.getDocumentsByQuery(args["query"], skipCount, loadCount);

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