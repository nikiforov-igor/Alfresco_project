var node = search.findNode(args["nodeRef"]);

var results = [];

var staffLists = node.getChildAssocsByType("lecm-orgstr:staff-list");

for (var index in staffLists){
    var staff = staffLists[index];
    var employeeLink = staff.getChildAssocsByType("lecm-orgstr:employee-link");
    if(!employeeLink || !employeeLink[0]){
        results.push(staff);
    }
}

model.staffs = results;