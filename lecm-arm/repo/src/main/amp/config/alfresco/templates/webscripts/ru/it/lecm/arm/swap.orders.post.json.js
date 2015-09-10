var firstNode = search.findNode(json.get("firstNodeRef"));
var secondNode = search.findNode(json.get("secondNodeRef"));

if ((firstNode != null) && (secondNode != null)) {

	var firstNodeOrder = firstNode.properties["lecm-arm:order"];
	var secondNodeOrder = secondNode.properties["lecm-arm:order"];

	if ((firstNodeOrder != null) && (secondNodeOrder != null)) {
		var temp = firstNode.properties["lecm-arm:order"];

		firstNode.properties["lecm-arm:order"] = secondNode.properties["lecm-arm:order"];

		secondNode.properties["lecm-arm:order"] = temp;

		firstNode.save();
		secondNode.save();
	}
}