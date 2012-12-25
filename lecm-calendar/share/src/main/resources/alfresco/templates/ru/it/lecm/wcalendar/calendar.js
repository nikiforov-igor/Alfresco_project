//скрипт для получения корневой (корневых) папок
var jsonStr = remote.connect("alfresco").get("/lecm/wcalendar/calendar/get/container");
//var obj = jsonUtils.toObject (jsonStr);

model.calendarContainer = jsonStr;
