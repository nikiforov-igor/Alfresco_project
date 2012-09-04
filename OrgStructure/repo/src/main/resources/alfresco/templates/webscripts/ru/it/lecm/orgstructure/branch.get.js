var query = "";
if (args["nodeRef"] == null || args["nodeRef"] == "") {
    query = "PATH: \"/app:company_home/cm:Organization/*\"";
} else {
    query = "PARENT: \"" + args["nodeRef"] + "\"";
}

var sort = {
    column: "@{http://www.alfresco.org/model/content/1.0}name",
    ascending: true
}

var def = {
    query: query + " AND TYPE: \"lecm-orgstructure:organization_unit\"",
    sort: [sort]
}
var divisions = search.query(def);

def = {
    query: query + " AND TYPE: \"lecm-orgstructure:employee\"",
    sort: [sort]
}
var members = search.query(def);

var branch = [];

addItems(branch, divisions);
addItems(branch, members);

model.branch = branch;

function addItems(branch, items) {
    for each(var item in items) {
        title = item.getName();
        type = getNodeType(item);
        nodeRef = item.getNodeRef().toString();
        isLeaf = true;
        if (type == "organization_unit") {
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
    var type = "employee"
    if (node.getTypeShort() == "lecm-orgstructure:organization") {
        type = "organization";
    } else if (node.getTypeShort() == "lecm-orgstructure:organization_unit") {
        type = "organization_unit"
    }
    return type;
}
