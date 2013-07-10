var type = template.properties.docType ? template.properties.docType : page.url.args.doctype;
model.docType = type;

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
    model.settings = remote.connect("alfresco").get("/lecm/document-type/settings?docType=" + type);

    var PREFERENCE_DOCUMENTS = "ru.it.lecm.documents.";
    var PREFERENCE_DOCUMENTS_STATUSES = "ru.it.lecm.documents." + type.split(":").join("_") + ".documents-list-statuses-filter";

    var prefStr = remote.connect("alfresco").get("/api/people/" + encodeURIComponent(user.id) + "/preferences?pf=" + PREFERENCE_DOCUMENTS);
    model.preferences = prefStr;
    /*if (prefStr.status == 200) {
        var prefStr = eval("(" + rolesStr + ")");
        var preference = findValueByDotNotation(prefStr, PREFERENCE_DOCUMENTS_STATUSES);
        if (preference != null) {
            model.prefQuery = preference;
        }
    }*/

}