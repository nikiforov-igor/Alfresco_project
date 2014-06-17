function main() {
	var urlArgs = {};
	var nodeRef = "";
	var formId = "";
	for (var prop in page.url.args) {
		if (prop == "nodeRef") {
			nodeRef = page.url.args[prop];
		} else if (prop == "formId") {
			formId = page.url.args[prop];
		} else {
			urlArgs[prop] = page.url.args[prop];
		}
	}

	var documentEdit = {
		name : "LogicECM.module.Documents.Edit",
		options : {
			nodeRef : nodeRef,
			formId : formId,
			args : urlArgs
		}
	};

	var documentPreview = {
		name : "LogicECM.control.Preview",
		initArgs: ["\"" + args["htmlid"] + "-preview\"" ]
	};

	model.widgets = [documentEdit, documentPreview];
}

main();