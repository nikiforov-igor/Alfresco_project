function main() {
	model.hasPermission = isStarter(page.url.args.documentType);
	if (!model.hasPermission) {
		model.accessMsg = "У вас нет прав на создание документов этого типа";
	}
}

function isStarter(docType) {
	var url = '/lecm/documents/employeeIsStarter?docType=' + docType;
	var result = remote.connect("alfresco").get(url);
	if (result.status != 200) {
		return false;
	}
	var perm = eval('(' + result + ')');
	return (("" + perm) == "true");
}

main();