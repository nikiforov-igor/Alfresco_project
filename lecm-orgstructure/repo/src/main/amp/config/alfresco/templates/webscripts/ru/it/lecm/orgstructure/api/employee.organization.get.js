var employee = search.findNode(args["nodeRef"]);

if (employee != null) {
    var contractorAssoc = employee.assocs["lecm-orgstr-aspects:linked-organization-assoc"];
    if (contractorAssoc != null && contractorAssoc.length > 0) {
        var contractor = contractorAssoc[0];
        var organizationAssoc = contractor.sourceAssocs["lecm-orgstr-aspects:linked-organization-assoc"];
        if (organizationAssoc != null && organizationAssoc.length > 0) {
            for (var i = 0; i < organizationAssoc.length; i++) {
                var parent = organizationAssoc[i];
                if (parent.typeShort == "lecm-orgstr:organization-unit") {
                    model.organization = parent;
                }
            }
        }
    }
}


