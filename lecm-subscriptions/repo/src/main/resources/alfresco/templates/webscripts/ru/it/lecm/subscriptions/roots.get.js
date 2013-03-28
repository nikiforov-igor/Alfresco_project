var roots = [];
var nodes = subscription.getRoots();
//process response
var oNodes = eval("(" + nodes + ")");

addItems(roots, oNodes);

model.roots = roots;

function addItems(branch, items) {
    for (var index in items) {
        page = (items[index].page != null ? items[index].page : "");
        nodeRef = items[index].nodeRef;
        itemType = (items[index].itemType != null ? items[index].itemType : "");
        deleteNode = (items[index].deleteNode != null && items[index].deleteNode != undefined ? items[index].deleteNode : false);
        branch.push({
            page:page,
            nodeRef:nodeRef,
            itemType:itemType,
            deleteNode:"" + deleteNode
        });
    }
}
