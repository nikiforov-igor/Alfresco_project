var query = "";
if (args["nodeRef"] == null || args["nodeRef"] == "") {
	query = "PATH: \"/app:company_home/cm:Dictionary/cm:Organization/*\"";
} else {
	query = "PARENT: \"" + args["nodeRef"] + "\"";
}

var sort = {
	column: "@{http://www.alfresco.org/model/content/1.0}name",
	ascending: true
}

var def = {
	query: query + " AND TYPE: \"lecm-dic:dictionary_values\"",
	sort: [sort]
}
var dictionary = search.query(def);

var items = [];

model.items = items;

for each(var item in dictionary) {
	name = item.properties["lecm-dic:name_values"];
	description = item.properties["lecm-dic:description_values"];
	active = item.properties["lecm-dic:active_values"];

	items.push({
		name:name,
		description:description,
		active:active
	})
}