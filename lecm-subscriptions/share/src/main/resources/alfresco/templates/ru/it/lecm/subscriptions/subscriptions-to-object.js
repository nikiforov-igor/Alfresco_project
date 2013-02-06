var rolesStr = remote.connect ("alfresco").get ("/lecm/orgstructure/api/getCurrentEmployeeRoles");

var rolesList = eval("(" + rolesStr + ")");
var hasRole = false;
for (var i = 0; i < rolesList.length; i++) {
    if (rolesList[i].id == "BR_SUBSCRIPTIONS_ENGINEER") {
        hasRole = true;
        break;
    }
}
model.isEngineer = hasRole;