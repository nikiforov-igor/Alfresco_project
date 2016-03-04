var contractor = search.findNode(args["nodeRef"]);
var linkedOrgsAssocs = contractor.sourceAssocs["lecm-orgstr-aspects:linked-organization-assoc"];

model.containsStaffList = false;
model.containsOrgUnits = false;

if (linkedOrgsAssocs) {
    for (var item in linkedOrgsAssocs) {
        var linkedOrg = linkedOrgsAssocs[item];
        if (linkedOrg.properties['lecm-dic:active']) {
            var type = "" + linkedOrg.getTypeShort();
            if (type == "lecm-orgstr:organization-unit" && !orgstructure.getRootUnit().nodeRef.equals(linkedOrg.parent.nodeRef)) {
                model.containsOrgUnits = true;
                break;
            } else if (type == "lecm-orgstr:staff-list") {
                model.containsStaffList = true;
                break;
            }
        }
    }
}