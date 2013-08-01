var nodeRef = args['nodeRef'];
var document = search.findNode(nodeRef);

var filter = args['filter'];

var myErrands = errands.getMyDocumentErrands(document, filter);
model.myErrands = myErrands;
model.myErrandsCount = myErrands.length;