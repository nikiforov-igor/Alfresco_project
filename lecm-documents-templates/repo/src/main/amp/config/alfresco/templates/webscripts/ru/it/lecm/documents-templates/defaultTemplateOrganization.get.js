var hasGlobalAccess = orgstructure.hasGlobalOrganizationsAccess();
if (!hasGlobalAccess) {
    model.organization = orgstructure.getEmployeeOrganization(orgstructure.getCurrentEmployee())
} else {
    var queryDef = {
        query: "TYPE:\"lecm-contractor:contractor-type\" AND ASPECT:\"lecm-orgstr-aspects:is-organization-aspect\" AND NOT @lecm\\-dic\\:active:false",
        language: "fts-alfresco",
        onerror: "no-results"
    };

    var organizations = search.query(queryDef);
    if (organizations.length == 1) {
        model.organization = organizations[0];
    }
}
