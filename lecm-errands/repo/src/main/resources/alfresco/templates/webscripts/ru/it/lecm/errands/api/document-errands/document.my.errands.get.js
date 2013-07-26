var nodeRef = args['nodeRef'];
var document = search.findNode(nodeRef);

var statusesParam = args['statuses'];
var statuses = (statusesParam != null) ? statusesParam.split(",") : [];

var myErrands = errands.getMyDocumentErrands(document, statuses);
model.myErrands = myErrands;
model.myErrandsCount = myErrands.length;

if (myErrands.length > 0) {
    var latestErrand = myErrands[myErrands.length - 1];
    model.latestErrandNoderef = latestErrand.nodeRef.toString();
    model.latestErrandStartDate = latestErrand.properties["cm:created"];
}