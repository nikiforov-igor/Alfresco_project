model.isEngineer = false;
var isEngineer = remote.connect("alfresco").get("/lecm/workflow/routes/isEngineer");
if (isEngineer.status == 200) {
	var nativeObject = eval("(" + isEngineer + ")");
	model.isEngineer = nativeObject.isEngineer;
}