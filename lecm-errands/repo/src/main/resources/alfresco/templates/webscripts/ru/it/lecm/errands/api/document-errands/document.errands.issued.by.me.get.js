var nodeRef = args['nodeRef'];
var document = search.findNode(nodeRef);

var statusesParam = args['statuses'];
var statuses = (statusesParam != null) ? statusesParam.split(",") : [];

var errandsIssuedByMe = errands.getDocumentErrandsIssuedByMe(document, statuses);
model.errandsIssuedByMe = errandsIssuedByMe;
model.errandsIssuedByMeCount = errandsIssuedByMe.length;
