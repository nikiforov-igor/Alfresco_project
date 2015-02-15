var rolesStr = remote.connect("alfresco").get("/lecm/orgstructure/api/getCurrentEmployeeRoles");
var settings = remote.connect("alfresco").get("/lecm/operative-storage/checkCentralized");
var isCentralized = false;
var rolesList = [];
if (rolesStr.status == 200) {
    rolesList = eval("(" + rolesStr + ")");
}

var isArchivist = false;
for (var i = 0; i < rolesList.length; i++) {
    if (rolesList[i].id == "DA_ARCHIVISTS") {
        isArchivist = true;
        break;
    }
}

if (settings.status == 200) {
    var json = eval("(" + settings + ")");
    isCentralized = json.isCentralized
}

model.isArchivist = isArchivist;
model.isCentralized = isCentralized;
