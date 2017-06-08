model.routesContainer = "";
var routesContainer = remote.connect("alfresco").get("/lecm/workflow/routes/getContainer");
if (routesContainer.status == 200) {
	model.routesContainer = routesContainer;
	model.allowEdit = user.isAdmin || JSON.parse(routesContainer).isEngineer;
}
