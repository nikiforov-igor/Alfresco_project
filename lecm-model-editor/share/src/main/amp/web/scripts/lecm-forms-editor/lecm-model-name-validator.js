if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.modelNameValidation = function (field, args, event, form, silent, message)
{
	if (!args) {
		args = {};
	}

	args.pattern = /(^[a-zA-Z0-9\-]+$)/;
	args.match = true;

	return Alfresco.forms.validation.regexMatch(field, args, event, form, silent, message);
};