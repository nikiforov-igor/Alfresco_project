function main() {
    model.hasPermission = hasDocumentViewPermission(page.url.args.nodeRef) && hasDocumentAttachmentViewPermission(page.url.args.nodeRef);
}

function hasDocumentViewPermission(nodeRef) {
	var url = '/lecm/document/api/getPermissions?nodeRef=' + page.url.args.nodeRef;
	var result = remote.connect("alfresco").get(url);
	if (result.status == 200) {
		var access = eval('(' + result + ')');
		if (access && (access.readAccess)) {
			return (("" + access.readAccess) == "true");
		}
	} else {
		return false;
	}
}

function hasDocumentAttachmentViewPermission(nodeRef) {
	var url = '/lecm/security/api/getPermission?nodeRef=' + nodeRef + '&permission=lecmPerm_ContentView';
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		return false;
	}
	var perm = eval('(' + result + ')');
	return (("" + perm) ==  "true");
}

main();