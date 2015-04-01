function main() {
	var url = '/lecm/document/api/getPermissions?nodeRef=' + page.url.args.nodeRef;
	var result = remote.connect("alfresco").get(url);
	model.hasPermission = false;
	if (result.status == 200) {
		var access = eval('(' + result + ')');
		if (access && (access.writeAccess)) {
			model.hasPermission = (("" + access.writeAccess) == "true");
		}
	}
}

main();