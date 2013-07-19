model.roles = [];
model.isEngineer = false;
model.isBoss = false;
var roles = remote.connect("alfresco").get("/lecm/wcalendar/absence/get/roles");
if (roles.status == 200) {
	var nativeObject = eval("(" + roles + ")");

	model.roles = roles;
	model.isEngineer = nativeObject.isEngineer;
	model.isBoss = nativeObject.isBoss;
}
