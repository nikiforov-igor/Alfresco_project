var settingsStr = remote.connect("alfresco").get("/lecm/orgstructure/root/org-roles");

var settings = eval("(" + settingsStr + ")");

model.settings = settings;
model.response = settingsStr;

var rolesStr = remote.connect ("alfresco").get ("/lecm/orgstructure/api/getCurrentEmployeeRoles");

var rolesList = eval("(" + rolesStr + ")");
var hasRole = false;
for (var i = 0; i < rolesList.length; i++) {
    if (rolesList[i].id == "BR_ORGSTRUCTURE_ENGINEER") {
        hasRole = true;
        break;
    }
}
model.isOrgEngineer = hasRole;
