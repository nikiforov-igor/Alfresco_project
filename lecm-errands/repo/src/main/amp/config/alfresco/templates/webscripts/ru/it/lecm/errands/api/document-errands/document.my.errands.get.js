var nodeRef = args['nodeRef'];
var document = search.findNode(nodeRef);
var maxItems = args["errandsLimit"] != null ? args["errandsLimit"] : -1;

var filter = args['filter'];

var k = 0;
var items = [];

var myErrands = errands.getMyDocumentErrands(document, filter);

if (myErrands != null && myErrands.length > 0) {
    for (var i = 0; i < myErrands.length; i++) {
        if (maxItems < 0 || k < maxItems) {
                items.push(myErrands[i]);
                k++;
        }
    }
}

model.myErrands = items;
model.errandsCount = myErrands.length;

if (myErrands.length > 0) {
    var latestErrand = myErrands[myErrands.length - 1];
    model.latestErrandNodeRef = latestErrand.nodeRef.toString();
    model.latestErrandStartDate = latestErrand.properties["cm:created"];
}