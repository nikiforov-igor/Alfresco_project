var rolesStr = remote.connect ("alfresco").get ("/lecm/orgstructure/api/getCurrentEmployeeRoles");

var rolesList = eval("(" + rolesStr + ")");
var hasRole = false;
for (var i = 0; i < rolesList.length; i++) {
    if (rolesList[i].id == "CONTRACT_USER") {
        hasRole = true;
        break;
    }
}
model.hasPermission = hasRole;