
var settings = remote.connect("alfresco").get("/lecm/reports-editor/settings");
if (settings.status == 200) {
    model.settings = settings;
}

model.currentUser = user.id;
