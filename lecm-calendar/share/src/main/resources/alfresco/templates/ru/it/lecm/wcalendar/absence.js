//скрипт для получения корневой (корневых) папок
var absenceContainer = remote.connect("alfresco").get("/lecm/wcalendar/absence/get/container");
var roles = remote.connect("alfresco").get("/lecm/wcalendar/absence/get/roles");

var nativeObject = eval("(" + roles + ")");

model.absenceContainer = absenceContainer;
model.absenceRoles = roles;
model.isEngineer = nativeObject.isEngineer;
model.isBoss = nativeObject.isBoss;
