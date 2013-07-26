var nodeRef = args['nodeRef'];
var document = search.findNode(nodeRef);

var myErrands = errands.getMyDocumentErrands(document, ["В работе", "На доработке", "На утверждении контролером", "На утверждении инициатором"]);
model.myErrands = myErrands;
model.myErrandsCount = myErrands.length;

if (myErrands.length > 0) {
    var latestErrand = myErrands[myErrands.length - 1];
    model.latestErrandNoderef = latestErrand.nodeRef.toString();
    model.latestErrandStartDate = latestErrand.properties["cm:created"];
}