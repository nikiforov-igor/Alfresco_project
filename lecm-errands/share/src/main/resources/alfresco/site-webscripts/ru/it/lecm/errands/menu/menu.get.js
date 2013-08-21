<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">
var rolesStr = remote.connect ("alfresco").get ("/lecm/orgstructure/api/getCurrentEmployeeRoles");

var rolesList = eval("(" + rolesStr + ")");
var hasRole = false;
for (var i = 0; i < rolesList.length; i++) {
    var roleStr = rolesList[i].id;
    if (roleStr.indexOf("ERRANDS_") == 0) {
        hasRole = true;
        break;
    }
}

var currentEmployee = doGetCall("/lecm/orgstructure/api/getCurrentEmployee");
model.isBoss = (currentEmployee!=null) ? currentEmployee["is-boss"] : "false";

model.hasPermission = hasRole;