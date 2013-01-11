if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Shedule = LogicECM.module.Shedule || {};


LogicECM.module.Shedule.timeValidation = function time(field, args, event, form, silent, message) {
	if (Alfresco.logger.isDebugEnabled())
		Alfresco.logger.debug("Validating field '" + field.id + "' with custom time validator");

	if (!args) {
		args = {};
	}
	//	  TODO: Добавить проверку того, что время окончания работы больше времени начала работы
	if (field.value.length < 1) {
		return false;
	}

	args.pattern = /^([0-1]\d|2[0-3]):[0-5]\d(:[0-5]\d)?$/;
	args.match = true;

	return Alfresco.forms.validation.regexMatch(field, args, event, form, silent, message);
};