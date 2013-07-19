//скрипт для получения корневой (корневых) папок
model.scheduleContainer = {};
model.calendarRoles = [];
model.isEngineer = false;
model.isBoss = false;
var scheduleContainer = remote.connect("alfresco").get("/lecm/wcalendar/schedule/get/container");
if (scheduleContainer.status == 200) {
	var roles = remote.connect("alfresco").get("/lecm/wcalendar/schedule/get/roles");
	if (roles.status == 200) {
		var nativeObject = eval("(" + roles + ")");

		model.scheduleContainer = scheduleContainer;
		model.calendarRoles = roles;
		model.isEngineer = nativeObject.isEngineer;
		model.isBoss = nativeObject.isBoss;
	}
}
