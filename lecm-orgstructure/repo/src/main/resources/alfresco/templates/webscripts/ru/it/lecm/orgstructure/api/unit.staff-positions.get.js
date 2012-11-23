var withDuplicates = [];
var node = search.findNode(args["nodeRef"]);
var staffLists = node.getChildAssocsByType("lecm-orgstr:staff-list");
for (var x in staffLists){
    withDuplicates.push(staffLists[x].assocs["lecm-orgstr:element-member-position-assoc"][0]);
}
var used = {};
var results = withDuplicates.filter(function(obj) {
    return obj.id in used ? 0:(used[obj.id]=1);
});
model.positions = results;
model.positionsCount = results.length;