var nodeRef = args['nodeRef'];
var document = search.findNode(nodeRef);
var maxItems = args["errandsLimit"] != null ? args["errandsLimit"] : -1;

var k = 0;
var items = [];

var errandsList = errands.getDocumentErrandsAll(document, null);

if (errandsList != null && errandsList.length > 0) {
	for (var i = 0; i < errandsList.length; i++) {
		if (maxItems < 0 || k < maxItems) {
			items.push(errandsList[i]);
			k++;
		}
	}
}

model.errandsList = items;
model.errandsCount = errandsList.length;

if (errandsList.length > 0) {
	var latestErrand = errandsList[errandsList.length - 1];
	model.latestErrandNodeRef = latestErrand.nodeRef.toString();
	model.latestErrandStartDate = latestErrand.properties["cm:created"];
}