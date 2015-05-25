function main() {
	model.hasPermission = false;

	var urlArgs = {};
	var docType = "";
	var formId = "";

	var connectionType = null;
	var connectionIsSystem = null;
	var connectionIsReverse = null;
	var parentDocumentNodeRef = null;
	var workflowTask = null;

	var actionType = null;
	var taskId = null;
	var actionId = null;

	var backUrl = null;

	var params = page.url.args["p1"];
	var decodeParams = new java.lang.String(Packages.org.apache.commons.codec.binary.Base64.decodeBase64(params));

	var hash = page.url.args["p2"];
	if (hash == (new Packages.java.lang.String(decodeParams)).hashCode()) {
		var paramsArray = decodeParams.split("&");
		for (var i in paramsArray) {
			var param = paramsArray[i];
			var name = param.split("=")[0];
			var value = param.split("=")[1];
			if (name && value) {
				if (name == "documentType") {
					docType = value;
				} else if (name == "formId") {
					formId = value;
				} else if (name == "connectionType") {
					connectionType = value;
				} else if (name == "connectionIsSystem") {
					connectionIsSystem = value;
				} else if (name == "connectionIsReverse") {
					connectionIsReverse = value;
				} else if (name == "parentDocumentNodeRef") {
					parentDocumentNodeRef = value;
				} else if (name == "workflowTask") {
					workflowTask = value;
				} else if (name == "actionType") {
					actionType = value;
				} else if (name == "taskId") {
					taskId = value;
				} else if (name == "actionId") {
					actionId = value;
				} else if (name == "backUrl") {
					backUrl = value;
				} else {
					try {
						urlArgs[name] = decodeURIComponent(value);
					} catch (e) {
						urlArgs[name] = value;
					}
				}
			}
		}

		model.hasPermission = isStarter(docType);
		if (model.hasPermission) {
			var documentCreate = {
				name: "LogicECM.module.Calendar.Create",
				options: {
					documentType: docType,
					formId: formId,
					connectionType: connectionType,
					connectionIsSystem: connectionIsSystem == 'true',
					connectionIsReverse: connectionIsReverse == 'true',
					parentDocumentNodeRef: parentDocumentNodeRef,
					workflowTask: workflowTask,
					actionType: actionType,
					actionId: actionId,
					taskId: taskId,
					backUrl: backUrl,
					args: urlArgs
				}
			};

			var documentPreview = {
				name: "LogicECM.control.Preview",
				initArgs: ["\"" + args["htmlid"] + "-preview\"" ]
			};

			model.widgets = [documentCreate, documentPreview];
		} else {
			model.accessMsg = "msg.create_not_allowed";
		}
	} else {
		model.accessMsg = "msg.create_not_allowed";
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
