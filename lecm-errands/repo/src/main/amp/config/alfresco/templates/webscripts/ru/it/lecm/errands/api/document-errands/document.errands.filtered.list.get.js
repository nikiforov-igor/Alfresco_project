var nodeRef = args['nodeRef'];
var document = search.findNode(nodeRef);

var filter = args['filter'];

model.meErrands = errands.getMyDocumentErrands(document, filter);
model.issuedMeErrands = errands.getDocumentErrandsIssuedByMe(document, filter);
model.controlledMeErrands = errands.getDocumentErrandsControlledMe(document, filter);
model.otherErrands = errands.getDocumentErrandsOther(document, filter);
