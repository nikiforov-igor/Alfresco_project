var nodeRef = args['nodeRef'];
var document = search.findNode(nodeRef);

var filter = args['filter'];

var errandsIssuedByMe = errands.getDocumentErrandsIssuedByMe(document, filter);
model.errandsIssuedByMe = errandsIssuedByMe;
model.errandsIssuedByMeCount = errandsIssuedByMe.length;
