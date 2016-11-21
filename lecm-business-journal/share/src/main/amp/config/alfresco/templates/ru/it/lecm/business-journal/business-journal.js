var rolesStr = remote.connect("alfresco").get("/lecm/orgstructure/api/getCurrentEmployeeRoles");

var rolesList = [];
if (rolesStr.status == 200) {
    rolesList = eval("(" + rolesStr + ")");
}
var hasRole = false;
for (var i = 0; i < rolesList.length; i++) {
    if (rolesList[i].id == "BR_BUSINESS_JOURNAL_ENGENEER") {
        hasRole = true;
        break;
    }
}
model.isEngineer = hasRole;
