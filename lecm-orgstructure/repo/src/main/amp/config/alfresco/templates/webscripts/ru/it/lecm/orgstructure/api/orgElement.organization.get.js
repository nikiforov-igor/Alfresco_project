var org = search.findNode(args["nodeRef"]);

if (org != null) {
    var contractorAssoc = org.assocs["lecm-orgstr-aspects:linked-organization-assoc"];
    if (contractorAssoc != null && contractorAssoc.length > 0) {
        model.organization = contractorAssoc[0];
    }
}


