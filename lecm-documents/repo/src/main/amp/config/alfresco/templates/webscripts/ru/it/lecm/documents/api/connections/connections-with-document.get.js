var documentNodeRef = args['documentNodeRef'];
var exclude = 'true' == args['exclErrands'];

var document = search.findNode(documentNodeRef);
var excludeErrands = document && document.isSubType("lecm-eds-document:base") && exclude;

model.documentService = documentScript;
var connectDocs = documentConnection.getConnectionsWithDocument(documentNodeRef);

if (excludeErrands) {
    var result = [];
    for (var i = 0; i < connectDocs.length; i++) {
        if (!connectDocs[i].isSubType("lecm-errands:document")) {
            result.push(connectDocs[i]);
        }
    }
    model.items = result;
} else {
    model.items = connectDocs;
}