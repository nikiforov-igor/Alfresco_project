//скрипт для получения корневой (корневых) папок
var calendarContainer = remote.connect("alfresco").get("/lecm/wcalendar/calendar/get/container");
var roles = remote.connect("alfresco").get("/lecm/wcalendar/calendar/get/roles");

var nativeObject = eval("(" + roles + ")");

model.calendarContainer = calendarContainer;
model.calendarRoles = roles;
model.isEngineer = nativeObject.isEngineer;
