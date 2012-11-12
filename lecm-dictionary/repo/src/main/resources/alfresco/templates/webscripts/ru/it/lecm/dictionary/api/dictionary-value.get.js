var nodeRef = args["nodeRef"];

if (nodeRef != null && nodeRef.length > 0) {
	var dicValue = search.findNode(nodeRef);

	if (dicValue != null) {
		var item = {
			node: dicValue,
			propertiesName: dicValue.getPropertyNames(true)
		}
		model.item = item;
	}
}