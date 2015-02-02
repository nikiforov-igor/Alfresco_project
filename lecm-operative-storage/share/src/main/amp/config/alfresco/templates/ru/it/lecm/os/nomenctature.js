var rolesStr = remote.connect("alfresco").get("/lecm/orgstructure/api/getCurrentEmployeeRoles");

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
model.isArchivist = isArchivist;
