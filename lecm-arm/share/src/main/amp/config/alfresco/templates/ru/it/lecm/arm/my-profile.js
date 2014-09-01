var settingsStr = remote.connect("alfresco").get("/lecm/document-type/settings?docType=lecm-document:base&archive=false");
if (settingsStr.status == 200) {
	model.settings = settingsStr;
}

model.currentUser = user.id;