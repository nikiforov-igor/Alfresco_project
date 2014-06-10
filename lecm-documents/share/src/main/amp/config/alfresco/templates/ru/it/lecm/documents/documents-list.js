var type = template.properties.docType ? template.properties.docType : page.url.args.doctype;
model.docType = type;
model.preferedFilter = template.properties.preferedFilter ? template.properties.preferedFilter : "docAuthor";

var hasRole = false;
var userPermissions = "";

var permissionsStr = remote.connect("alfresco").get("/lecm/documents/accessPermissions?docType=" + type);
if (permissionsStr.status == 200) {
    var permissionsList = eval("(" + permissionsStr + ")");
    for (var i = 0; i < permissionsList.length; i++) {
        userPermissions = userPermissions + permissionsList[i].id + ",";
    }

    if (userPermissions != "") {
        var rolesStr = remote.connect("alfresco").get("/lecm/orgstructure/api/getCurrentEmployeeRoles");
        if (rolesStr.status == 200) {
            var rolesList = eval("(" + rolesStr + ")");
            for (var j = 0; j < rolesList.length; j++) {
                if (userPermissions.indexOf(rolesList[j].id) >= 0) {
                    hasRole = true;
                    break;
                }
            }
        }
    } else {
        hasRole = true;
    }
}

model.hasPermission = hasRole;


if (hasRole) {
    var settingsStr = remote.connect("alfresco").get("/lecm/document-type/settings?docType=" + type + "&archive=false");
    if (settingsStr.status == 200) {
        model.settings = settingsStr;

        var settingsObj = eval("(" + settingsStr + ")");
        if (settingsObj) {
            model.defaultFilter = settingsObj.defaultFilter;
            model.defaultKey = settingsObj.defaultKey;
        }
    }
}

model.currentUser = user.id;