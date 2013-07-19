//скрипт для получения корневой (корневых) папок
model.calendarContainer = {};
model.calendarRoles = [];
model.isEngineer = false;
var calendarContainer = remote.connect("alfresco").get("/lecm/wcalendar/calendar/get/container");
if (calendarContainer.status == 200) {
	var roles = remote.connect("alfresco").get("/lecm/wcalendar/calendar/get/roles");
	if (roles.status == 200) {
		var nativeObject = eval("(" + roles + ")");

		model.calendarContainer = calendarContainer;
		model.calendarRoles = roles;
		model.isEngineer = nativeObject.isEngineer;
	}
}
