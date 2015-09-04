var firstNode = search.findNode(json.get("firstNodeRef"));
var secondNode = search.findNode(json.get("secondNodeRef"));

if ((firstNode != null) && (secondNode != null)) {

	var firstNodeOrder = firstNode.properties["lecm-arm:field-order-number"];
	var secondNodeOrder = secondNode.properties["lecm-arm:field-order-number"];

	if ((firstNodeOrder != null) && (secondNodeOrder != null)) {
		var temp = firstNode.properties["lecm-arm:field-order-number"];

		firstNode.properties["lecm-arm:field-order-number"] = secondNode.properties["lecm-arm:field-order-number"];

		secondNode.properties["lecm-arm:field-order-number"] = temp;

		firstNode.save();
		secondNode.save();
	}
}