//скрипт для получения корневой (корневых) папок
model.absenceContainer = {};
model.absenceRoles = [];
model.isEngineer = false;
model.isBoss = false;
var absenceContainer = remote.connect("alfresco").get("/lecm/wcalendar/absence/get/container");
if (absenceContainer.status == 200) {
	var roles = remote.connect("alfresco").get("/lecm/wcalendar/absence/get/roles");
	if (roles.status == 200) {
		var nativeObject = eval("(" + roles + ")");

		model.absenceContainer = absenceContainer;
		model.absenceRoles = roles;
		model.isEngineer = nativeObject.isEngineer;
		model.isBoss = nativeObject.isBoss;
	}
}
