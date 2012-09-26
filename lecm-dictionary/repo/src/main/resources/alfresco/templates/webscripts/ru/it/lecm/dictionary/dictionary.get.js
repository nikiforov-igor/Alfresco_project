var query = "";
if (args["nodeRef"] == null || args["nodeRef"] == "") {
	query = "PATH: \"/app:company_home/cm:Dictionary/*\"";
} else {
	query = "PARENT: \"" + args["nodeRef"] + "\"";
}

var sort = {
	column: "@{http://www.alfresco.org/model/content/1.0}name",
	ascending: true
}

var def = {
	query: query + " AND TYPE: \"lecm-dic:dictionary\"",
	sort: [sort]
}
var dictionary = search.query(def);

def = {
	query: query + " AND TYPE: \"lecm-dic:dictionary_values\"",
	sort: [sort]
}
var dictionary_values = search.query(def);

var branch = [];

addItems(branch, dictionary);
addItems(branch, dictionary_values);

model.branch = branch;

function addItems(branch, items) {
	for each(var item in items) {
		title = item.getName();
		type = getNodeType(item);
		nodeRef = item.getNodeRef().toString();
		isLeaf = true;
		if (type == "dictionary") {
			isLeaf = !item.hasChildren;
		}
		branch.push({
			title: title,
			type: type,
			nodeRef: nodeRef,
			isLeaf: "" + isLeaf
		});
	}
}

function getNodeType(node) {
	var type = "dictionary_values"
	if (node.getTypeShort() == "lecm-dic:dictionary") {
		type = "dictionary";
	} else if (node.getTypeShort() == "lecm-dic:dictionary_values") {
		type = "dictionary_values"
	}
	return type;
}