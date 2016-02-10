var settingsStr = remote.connect("alfresco").get("/lecm/orgstructure/root/org-positions");

var settings = {};
if (settingsStr.status == 200) {
    settings = eval("(" + settingsStr + ")");
}

model.settings = settings;
model.response = settingsStr;

//var rolesStr = remote.connect("alfresco").get("/lecm/orgstructure/api/getCurrentEmployeeRoles");
//
//var rolesList = [];
//if (rolesStr.status == 200) {
//    rolesList = eval("(" + rolesStr + ")");
//}
//var hasRole = false;
//for (var i = 0; i < rolesList.length; i++) {
//    if (rolesList[i].id == "BR_ORGSTRUCTURE_ENGINEER") {
//        hasRole = true;
//        break;
//    }
//}
//model.isOrgEngineer = hasRole;
