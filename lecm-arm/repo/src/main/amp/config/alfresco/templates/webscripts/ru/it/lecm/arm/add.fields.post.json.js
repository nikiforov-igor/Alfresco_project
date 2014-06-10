var parentNodeRef = json.get("parentNodeRef");

var node = search.findNode(parentNodeRef);

var results = [];
if (node != null) {
	var fields = json.get("fields");

	if (fields != null) {
		for (var i = 0; i < fields.length(); i++) {
			var name = fields.get(i).get("name");
			var title = fields.get(i).get("title");
			if (name != null) {
				var properties = [];
				properties["lecm-arm:field-name"] = name;
				properties["lecm-arm:field-title"] = title;
				var createdObject = node.createNode(null, "lecm-arm:field", properties);
				results.push(createdObject);
			}
		}
	}
}
model.results = results;
