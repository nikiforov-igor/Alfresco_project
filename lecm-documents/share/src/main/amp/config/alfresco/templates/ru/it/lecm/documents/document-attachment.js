<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/document-utils.js">

function main() {
    model.hasPermission = hasDocumentViewPermission(page.url.args.nodeRef) && hasDocumentAttachmentViewPermission(page.url.args.nodeRef);
	model.dependencies = DocumentUtils.getDependencies("LecmDocumentDetails");
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
	var url = '/lecm/security/api/getPermission?nodeRef=' + nodeRef + '&permission=_lecmPerm_ContentView';
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		return false;
	}
	var permContentView = eval('(' + result + ')');
	var permReadAttachment = hasReadAttachmentPermission(nodeRef,user.id);
	var perm = permContentView && permReadAttachment;
	return (("" + perm) ==  "true");
}

main();