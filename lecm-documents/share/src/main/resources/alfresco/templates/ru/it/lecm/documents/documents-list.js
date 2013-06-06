var hasRole = false;
var userPermissions = "";
var type = template.properties.docType ? template.properties.docType : page.url.args.doctype;

var permissionsStr = remote.connect("alfresco").get("/lecm/documents/accessPermissions?docType=" + type);
if (permissionsStr.status == 200) {
    var permissionsList = eval("(" + permissionsStr + ")");
    for (var i = 0; i < permissionsList.length; i++) {
        userPermissions = userPermissions + permissionsList[i].id + ",";
    }
}

var rolesStr = remote.connect("alfresco").get("/lecm/orgstructure/api/getCurrentEmployeeRoles");
if (rolesStr.status == 200) {
    var rolesList = eval("(" + rolesStr + ")");
    for (var i = 0; i < rolesList.length; i++) {
        if (userPermissions.indexOf(rolesList[i].id) >= 0) {
            hasRole = true;
            break;
        }
    }
}
model.hasPermission = hasRole;

if (hasRole) {
    model.settings = remote.connect("alfresco").get("/lecm/document-type/settings?docType=" + type);
}
