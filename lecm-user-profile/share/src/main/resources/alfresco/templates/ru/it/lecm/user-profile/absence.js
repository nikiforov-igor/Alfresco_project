//скрипт для получения корневой (корневых) папок
var absenceContainer = remote.connect("alfresco").get("/lecm/wcalendar/absence/get/container");
if (absenceContainer.status == 200) {
	model.absenceContainer = absenceContainer;
} else {
    model.absenceContainer = {};
}
