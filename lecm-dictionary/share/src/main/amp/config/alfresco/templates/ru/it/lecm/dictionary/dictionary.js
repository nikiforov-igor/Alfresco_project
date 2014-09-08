var rolesStr = remote.connect("alfresco").get("/lecm/orgstructure/api/getCurrentEmployeeRoles");

var rolesList = [];
if (rolesStr.status ==200) {
    rolesList = eval("(" + rolesStr + ")");
}
var hasRole = false;
for (var i = 0; i < rolesList.length; i++) {
    if (rolesList[i].id == "BR_DICTIONARIES_ENGINEER") {
        hasRole = true;
        break;
    }
}

var planeRequest = remote.connect("alfresco").get("/lecm/dictionary/api/getDictionary?dicName=" + encodeURI(page.url.args["dic"]));
var plane = true;

if (planeRequest.status ==200) {
    planeObject = eval("(" + planeRequest + ")");
    plane = planeObject.plane.toLowerCase() == "true";
}
model.plane = plane;
model.isEngineer = hasRole;