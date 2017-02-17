var errand = search.findNode(args['nodeRef']);

if (errand) {
    model.reportText = errand.properties["lecm-errands:execution-report"];

    var connectedDocuments = [];
    var errandConnectedDocuments = errand.assocs["lecm-errands:execution-connected-document-assoc"];
    if (errandConnectedDocuments && errandConnectedDocuments.length) {
        errandConnectedDocuments.forEach(function (doc) {
            connectedDocuments.push({
                doc: doc,
                viewPage: documentScript.getViewUrl(doc)
            });
        });
    }
    model.connectedDocuments = connectedDocuments;
    model.attachments = errand.assocs["lecm-errands:execution-report-attachment-assoc"];
}