var branch = [];
var nodes = orgstructure.getArmStructure(args["nodeRef"] != null ? args["nodeRef"] : "");
//process response
var oNodes = eval("(" + nodes + ")");

addItems(branch, oNodes);

model.branch = branch;

function addItems(branch, items) {
    for (var index in items) {
        title = (items[index].title != null ? items[index].title : "");
        label = (items[index].label != null ? items[index].label : "");
        type = (items[index].itemType != null ? items[index].itemType : "");
        nodeRef = items[index].nodeRef;
        isLeaf = (items[index].isLeaf != null ? items[index].isLeaf : true);
        expand = (items[index].expand != null ? items[index].expand : false);
        branch.push({
            title:title,
            label:label,
            type:type,
            nodeRef:nodeRef,
            isLeaf:"" + isLeaf,
            expand: "" + expand
        });
    }
}
