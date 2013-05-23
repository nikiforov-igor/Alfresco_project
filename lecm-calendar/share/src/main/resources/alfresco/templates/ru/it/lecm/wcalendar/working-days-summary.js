var roles = remote.connect("alfresco").get("/lecm/wcalendar/absence/get/roles");

var nativeObject = eval("(" + roles + ")");

model.roles = roles;
model.isEngineer = nativeObject.isEngineer;
model.isBoss = nativeObject.isBoss;
