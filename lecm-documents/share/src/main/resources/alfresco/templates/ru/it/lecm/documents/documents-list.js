var hasRole = false;
var rolesStr = remote.connect("alfresco").get("/lecm/orgstructure/api/getCurrentEmployeeRoles");
if (rolesStr.status == 200) {
    var rolesList = eval("(" + rolesStr + ")");
    for (var i = 0; i < rolesList.length; i++) {
        var roleStr = rolesList[i].id;
        if (roleStr.indexOf(template.properties.rolesPrefix) == 0) {
            hasRole = true;
            break;
        }
    }
}
model.hasPermission = hasRole;

if (hasRole) {
    model.settings = remote.connect("alfresco").get(template.properties.settingsScript);
}
