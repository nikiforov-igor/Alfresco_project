var organization = orgstructure.getOrganizationDirectory();
var branch = [];
var nodes;
if (args["nodeRef"] == null || args["nodeRef"] == "") {
    var orgFolderRef = organization.getNodeRef().toString();
    nodes = orgstructure.getStructure("organization",orgFolderRef);
} else {
    nodes = orgstructure.getStructure("organization-unit", args["nodeRef"]);
}
//process response
var oNodes = eval("(" + nodes + ")");

addItems(branch, oNodes);

model.branch = branch;

function addItems(branch, items) {
    for (var index in items) {
        title = (items[index].title != null ? items[index].title : "");
        type = (items[index].itemType != null ? items[index].itemType : "");
        nodeRef = items[index].nodeRef;
        isLeaf = (items[index].isLeaf != null ? items[index].isLeaf : true);
        branch.push({
            title:title,
            type:type,
            nodeRef:nodeRef,
            isLeaf:"" + isLeaf
        });
    }
}
