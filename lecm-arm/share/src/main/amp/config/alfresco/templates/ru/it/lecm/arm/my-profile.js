var settingsStr = remote.connect("alfresco").get("/lecm/document-type/settings?docType=lecm-document:base&archive=false");
if (settingsStr.status == 200) {
	model.settings = settingsStr;
}

model.code = "lecm_my_profile";
if (page.url.args["path"] != null && page.url.args["path"] != "") {
	var path = remote.connect("alfresco").get("/lecm/arm/convert?code=" + encodeURI(model.code) + "&path=" + encodeURI(page.url.args["path"]));
	if (path.status == 200) {
		model.path = path;
	}
}

model.currentUser = user.id;