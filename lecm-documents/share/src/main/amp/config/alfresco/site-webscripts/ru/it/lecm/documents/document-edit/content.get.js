function main() {
	var args = {};
	var nodeRef = "";
	var formId = "";
	for (var prop in page.url.args) {
		if (prop == "nodeRef") {
			nodeRef = page.url.args[prop];
		} else if (prop == "formId") {
			formId = page.url.args[prop];
		} else {
			args[prop] = page.url.args[prop];
		}
	}

	var widget = {
		name : "LogicECM.module.Documents.Edit",
		options : {
			nodeRef : nodeRef,
			formId : formId,
			args : args
		}
	};

	model.widgets = [widget];
}

main();