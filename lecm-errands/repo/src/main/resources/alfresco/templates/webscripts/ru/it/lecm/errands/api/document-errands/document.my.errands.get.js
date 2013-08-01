var nodeRef = args['nodeRef'];
var document = search.findNode(nodeRef);

var filter = args['filter'];

var myErrands = errands.getMyDocumentErrands(document, filter);
model.myErrands = myErrands;
model.errandsCount = myErrands.length;

if (myErrands.length > 0) {
    var latestErrand = myErrands[myErrands.length - 1];
    model.latestErrandNodeRef = latestErrand.nodeRef.toString();
    model.latestErrandStartDate = latestErrand.properties["cm:created"];
}