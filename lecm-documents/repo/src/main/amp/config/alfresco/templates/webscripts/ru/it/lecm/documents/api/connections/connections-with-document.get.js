var documentNodeRef = args['documentNodeRef'];
var exclude = 'true' == args['exclErrands'];
var checkAccess = 'true' == ('' + args['checkAccess']);

var document = search.findNode(documentNodeRef);
var excludeErrands = document && document.isSubType("lecm-eds-document:base") && exclude;

model.documentService = documentScript;
model.lecmPermission = lecmPermission;

var connectDocs = documentConnection.getConnectionsWithDocument(documentNodeRef, checkAccess);

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