//скрипт для получения корневой (корневых) папок
var sheduleContainer = remote.connect("alfresco").get("/lecm/wcalendar/shedule/get/container");
var roles = remote.connect("alfresco").get("/lecm/wcalendar/shedule/get/roles");

var nativeObject = eval("(" + roles + ")");

model.sheduleContainer = sheduleContainer;
model.calendarRoles = roles;
model.isEngineer = nativeObject.isEngineer;
model.isBoss = nativeObject.isBoss;
