var organization = orgstructure.getOrganizationDirectory();
var roots = [];
var nodes = nodes = orgstructure.getRoots();
//process response
var oNodes = eval("(" + nodes + ")");

addItems(roots, oNodes);

model.roots = roots;

function addItems(branch, items) {
    for (var index in items) {
        page = (items[index].page != null ? items[index].page : "");
        nodeRef = items[index].nodeRef;
        itemType = (items[index].itemType != null ? items[index].itemType : "");
        pattern = (items[index].namePattern != null ? items[index].namePattern : "");
        branch.push({
            page:page,
            nodeRef:nodeRef,
            itemType:itemType,
            pattern:pattern
        });
    }
}
