function main() {
	var urlArgs = {};
	var docType = "";
	var formId = "";
	for (var prop in page.url.args) {
		if (prop == "documentType") {
			docType = page.url.args[prop];
		} else if (prop == "formId") {
			formId = page.url.args[prop];
		} else {
			urlArgs[prop] = page.url.args[prop];
		}
	}

	var documentCreate = {
		name : "LogicECM.module.Documents.Create",
		options : {
			documentType : docType,
			formId : formId,
			args : urlArgs
		}
	};

	var documentPreview = {
		name : "LogicECM.control.Preview",
		initArgs: ["\"" + args["htmlid"] + "-preview\"" ]
	};

	model.widgets = [documentCreate, documentPreview];
}

main();