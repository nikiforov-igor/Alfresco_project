if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.repoRegexMatchColor = function (field, args, event, form, silent, message)
{
	args.pattern = args.expression;
	args.match = args.requiresMatch;

	var valid = Alfresco.forms.validation.regexMatch(field, args, event, form, silent, message);
	if (valid) {
		YAHOO.util.Dom.removeClass(field.id, "invalid");
	} else {
		YAHOO.util.Dom.addClass(field.id, "invalid");
	}

	return valid;
};