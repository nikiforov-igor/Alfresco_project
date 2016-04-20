(function () {
    var rolesStr = remote.connect("alfresco").get("/lecm/orgstructure/api/getCurrentEmployeeRoles");
    var rolesList = [];
    if (rolesStr.status == 200) {
        rolesList = eval("(" + rolesStr + ")");
    }
    var hasRole = false;
    for (var i = 0; i < rolesList.length; i++) {
        if (rolesList[i].id == "DA_ENGINEER") {
            hasRole = true;
            break;
        }
    }
    model.isEngineer = hasRole;

    var countersContainer = remote.connect("alfresco").get("/lecm/regnumbers/counters/getContainer");
    if (countersContainer.status == 200) {
        model.countersContainer = countersContainer;
    }
})();
