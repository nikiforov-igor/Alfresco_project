//скрипт для получения корневой (корневых) папок
var scheduleContainer = remote.connect("alfresco").get("/lecm/wcalendar/schedule/get/container");
var roles = remote.connect("alfresco").get("/lecm/wcalendar/schedule/get/roles");

var nativeObject = eval("(" + roles + ")");

model.scheduleContainer = scheduleContainer;
model.calendarRoles = roles;
model.isEngineer = nativeObject.isEngineer;
model.isBoss = nativeObject.isBoss;
