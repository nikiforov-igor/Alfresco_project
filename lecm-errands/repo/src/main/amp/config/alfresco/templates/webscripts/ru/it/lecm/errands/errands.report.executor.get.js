var errand = search.findNode(args['nodeRef']);

if (errand) {
    model.reportText = errand.properties["lecm-errands:execution-report"];
    model.connectedDocuments = errand.assocs["lecm-errands:execution-connected-document-assoc"];
    model.attachments = errand.assocs["lecm-errands:execution-report-attachment-assoc"];
}