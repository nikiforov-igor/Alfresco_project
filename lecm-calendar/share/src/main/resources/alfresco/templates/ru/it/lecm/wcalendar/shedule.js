//скрипт для получения корневой (корневых) папок
var jsonStr = remote.connect("alfresco").get("/lecm/wcalendar/shedule/get/container");
//var obj = jsonUtils.toObject (jsonStr);
model.sheduleContainer = jsonStr;