var rolesStr = remote.connect ("alfresco").get ("/lecm/orgstructure/api/getCurrentEmployeeRoles");

var rolesList = eval("(" + rolesStr + ")");
var hasRole = false;
for (var i = 0; i < rolesList.length; i++) {
    var roleStr = rolesList[i].id;
    if (roleStr.indexOf("CONTRACT_") == 0) {
        hasRole = true;
        break;
    }
}
model.hasPermission = hasRole;

if (hasRole) {
    model.settings = remote.connect("alfresco").get("/lecm/contracts/draft-root");
}
