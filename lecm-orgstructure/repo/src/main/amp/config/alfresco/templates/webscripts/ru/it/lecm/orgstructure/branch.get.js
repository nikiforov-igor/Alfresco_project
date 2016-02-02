/* global orgstructure, args, model */

var organization = orgstructure.getHolding();
var branch = [];
var nodes;
if (args["nodeRef"] == null || args["nodeRef"] == "") {
    var orgFolderRef = organization.getNodeRef().toString();
    nodes = orgstructure.getStructure("organization-unit", orgFolderRef);
} else {
    nodes = orgstructure.getStructure("organization-unit", args["nodeRef"]);
}
//process response
var oNodes = eval("(" + nodes + ")");

addItems(branch, oNodes);

model.branch = branch;

function addItems(branch, items) {
    for (var index in items) {
        var title = (items[index].title != null ? items[index].title : "");
        var label = (items[index].label != null ? items[index].label : "");
        var type = (items[index].itemType != null ? items[index].itemType : "");
        var nodeRef = items[index].nodeRef;
        var isLeaf = (items[index].isLeaf != null ? items[index].isLeaf : true);
        branch.push({
            title:title,
            label:label,
            type:type,
            nodeRef:nodeRef,
            isLeaf:"" + isLeaf
        });
    }
}
