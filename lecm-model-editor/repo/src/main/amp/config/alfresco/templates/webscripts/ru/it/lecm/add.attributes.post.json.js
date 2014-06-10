var formNodeRef = json.get("formNodeRef");

var form = search.findNode(formNodeRef);

var results = [];
if (form != null) {
	var attributes = json.get("attributes");

	if (attributes != null) {
		for (var i = 0; i < attributes.length(); i++) {
			var name = attributes.get(i).get("name");
			var title = attributes.get(i).get("title");
			if (name != null) {
				var properties = [];
				properties["lecm-forms-editor:attr-name"] = name;
				properties["lecm-forms-editor:attr-title"] = title;
				var node = form.createNode(null, "lecm-forms-editor:attr", properties);
				results.push(node);
			}
		}
	}
}
model.results = results;
