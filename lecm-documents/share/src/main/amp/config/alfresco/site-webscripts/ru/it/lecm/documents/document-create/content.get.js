function main() {
	model.hasPermission = false;

	var urlArgs = {};
	var docType = "";
	var formId = "";

	var connectionType = null;
	var connectionIsSystem = null;
	var parentDocumentNodeRef = null;

	var params = page.url.args["p1"];
	var decodeParams = new java.lang.String(Packages.org.apache.commons.codec.binary.Base64.decodeBase64(params));

	var hash = page.url.args["p2"];
	if (hash == (new Packages.java.lang.String(decodeParams)).hashCode()) {
		var paramsArray = decodeParams.split("&");
		for (var i in paramsArray) {
			var param = paramsArray[i];
			var name = param.split("=")[0];
			var value = param.split("=")[1];
			if (name != null && value != null) {
				if (name == "documentType") {
					docType = value;
				} else if (name == "formId") {
					formId = value;
				} else if (name == "connectionType") {
					connectionType = value;
				} else if (name == "connectionIsSystem") {
					connectionIsSystem = value;
				} else if (name == "parentDocumentNodeRef") {
					parentDocumentNodeRef = value;
				} else {
					urlArgs[name] = value;
				}
			}
		}

		model.hasPermission = isStarter(docType);
		if (model.hasPermission) {
			var documentCreate = {
				name: "LogicECM.module.Documents.Create",
				options: {
					documentType: docType,
					formId: formId,
					connectionType: connectionType,
					connectionIsSystem: connectionIsSystem,
					parentDocumentNodeRef: parentDocumentNodeRef,
					args: urlArgs
				}
			};

			var documentPreview = {
				name: "LogicECM.control.Preview",
				initArgs: ["\"" + args["htmlid"] + "-preview\"" ]
			};

			model.widgets = [documentCreate, documentPreview];
		} else {
			model.accessMsg = "У вас нет прав на создание документов этого типа";
		}
	} else {
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