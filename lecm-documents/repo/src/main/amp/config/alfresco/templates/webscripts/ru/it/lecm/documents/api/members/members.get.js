var documentRef = args['nodeRef'];
var skipItemsCount = parseInt(args["skipCount"]);
var loadItemsCount = parseInt(args["loadCount"]);

try {
    var members = documentMembers.getMembers(documentRef, skipItemsCount, loadItemsCount);
    var membersArray = [];
    for (var index in members) {
        var member = members[index];
        var employee = member.assocs["lecm-doc-members:employee-assoc"][0];
        var primaryPosition = orgstructure.getPrimaryPosition(employee.nodeRef);
        member.properties["employeePosition"] = (primaryPosition != null && primaryPosition.assocs["lecm-orgstr:element-member-position-assoc"] != null) ? primaryPosition.assocs["lecm-orgstr:element-member-position-assoc"][0].getName() : "";
        member.properties["employeeFIO"] = employee.properties["lecm-orgstr:employee-last-name"] + " " + employee.properties["lecm-orgstr:employee-first-name"] + " " + (employee.properties["lecm-orgstr:employee-middle-name"]!=null ? employee.properties["lecm-orgstr:employee-middle-name"] : "");
        member.properties["employeeRef"] = employee.nodeRef.toString();
        membersArray.push({
            nodeRef: member.nodeRef.toString(),
            group: member.properties["lecm-doc-members:group"],
            employeeName: member.properties["employeeFIO"],
            employeePosition: member.properties["employeePosition"],
            employeeRef: member.properties["employeeRef"]
        });
    }
    model.members = membersArray;
    model.next = documentMembers.getMembers(documentRef, skipItemsCount + loadItemsCount, 1);
} catch (e) {

}

