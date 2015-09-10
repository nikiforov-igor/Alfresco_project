var parentNodeRef = json.get("parentNodeRef");

var node = search.findNode(parentNodeRef);

var results = [];
if (node != null) {

	var fields = json.get("fields");
	var max = 0;
	var associations = node.assocs['lecm-arm:fields-assoc'];

	if(associations != null) {
		for(var i = 0; i < associations.length; i++) {
			var el = associations[i];
			if ((el.properties["lecm-arm:order"] != null) && (el.properties["lecm-arm:order"] >= max))
				max = el.properties["lecm-arm:order"] + 1;
		}
	}
	var orderNumber = max;

	if (fields != null) {
		for (var i = 0; i < fields.length(); i++) {
			var name = fields.get(i).get("name");
			var title = fields.get(i).get("title");
			if (name != null) {
				var properties = [];
				properties["lecm-arm:field-name"] = name;
				properties["lecm-arm:field-title"] = title;
				properties["lecm-arm:order"] = orderNumber++;
				var createdObject = node.createNode(null, "lecm-arm:field", properties);
				results.push(createdObject);
			}
		}
	}

}

model.results = results;