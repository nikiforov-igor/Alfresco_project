var request = jsonUtils.toObject(json);

var template = request.template;
var verbose = request.verbose != null ? request.verbose : false;

if (template != null) {
	var result = regnumbers.validateTemplate(template, verbose);
	if (result.length() > 0) {
		model.isValid = false;
		model.errorReason = result;
	} else {
		model.isValid = true;
	}
} else {
	model.isValid = false;
	status.code = 400;
	status.message = "Insifficient params!";
}
