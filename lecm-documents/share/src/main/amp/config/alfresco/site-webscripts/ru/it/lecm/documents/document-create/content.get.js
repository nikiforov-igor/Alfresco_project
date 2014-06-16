function main() {
	var args = {};
	var docType = "";
	var formId = "";
	for (var prop in page.url.args) {
		if (prop == "documentType") {
			docType = page.url.args[prop];
		} else if (prop == "formId") {
			formId = page.url.args[prop];
		} else {
			args[prop] = page.url.args[prop];
		}
	}

	var widget = {
		name : "LogicECM.module.Documents.Create",
		options : {
			documentType : docType,
			formId : formId,
			args : args
		}
	};

	model.widgets = [widget];
}

main();