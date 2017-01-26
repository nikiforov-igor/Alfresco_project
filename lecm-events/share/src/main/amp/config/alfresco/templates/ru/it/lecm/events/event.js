function main() {
	var url = '/lecm/document/api/getPermissions?nodeRef=' + page.url.args.nodeRef;
	var result = remote.connect("alfresco").get(url);
	model.hasPermission = false;
	if (result.status == 200) {
		var access = eval('(' + result + ')');
		if (access && (access.readAccess)) {
			model.hasPermission = (("" + access.readAccess) == "true");

			var errandsSettings = remote.connect("alfresco").get("/lecm/document-type/settings?docType=lecm-errands:document");
			if (errandsSettings.status == 200) {
				model.errandsSettings = errandsSettings;
			}
		}
	}

	url = '/lecm/documents/hasStatemachine?nodeRef=' + page.url.args.nodeRef;
	result = remote.connect("alfresco").get(url);
	if (result.status == 200) {
		var hasStatemachine = eval('(' + result + ')');
		model.hasStatemachine = hasStatemachine;
	} else {
		model.hasStatemachine = false;
	}
}

main();