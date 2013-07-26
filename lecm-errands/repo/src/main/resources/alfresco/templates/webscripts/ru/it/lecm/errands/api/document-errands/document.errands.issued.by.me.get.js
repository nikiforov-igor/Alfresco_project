var nodeRef = args['nodeRef'];
var document = search.findNode(nodeRef);

var errandsIssuedByMe = errands.getDocumentErrandsIssuedByMe(document, ["В работе", "На доработке", "На утверждении контролером", "На утверждении инициатором"]);
model.errandsIssuedByMe = errandsIssuedByMe;
model.errandsIssuedByMeCount = errandsIssuedByMe.length;
