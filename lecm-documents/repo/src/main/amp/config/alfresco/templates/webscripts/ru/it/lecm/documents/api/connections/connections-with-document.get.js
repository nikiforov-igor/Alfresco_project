var documentNodeRef = args['documentNodeRef'];
var exclude = args['exclErrands'] ? ("" + args['exclErrands']) == "true" : false;

var document = search.findNode(documentNodeRef);
var excludeErrands = document != null && document.isSubType("lecm-eds-document:base") && exclude;

model.documentService = documentScript;
var connectDocs = documentConnection.getConnectionsWithDocument(documentNodeRef);
var result = [];
for (var i = 0; i < connectDocs.length; i++) {
    if (!connectDocs[i].isSubType("lecm-errands:document") || !excludeErrands) {
        result.push(connectDocs[i]);
    }
}
model.items = result;