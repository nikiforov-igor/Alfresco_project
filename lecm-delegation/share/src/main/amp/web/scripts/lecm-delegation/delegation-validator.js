if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Delegation = LogicECM.module.Delegation || {};

LogicECM.module.Delegation.Validator = LogicECM.module.Delegation.Validator || {};

LogicECM.module.Delegation.Validator.Utils = LogicECM.module.Delegation.Validator.Utils || {};

(function () {
	"use strict";
	LogicECM.module.Delegation.Validator.HasTrusteeValidator = function (field, args,  event, form, silent, message) {
		form.setErrorContainer("error-message-container");
		// Каждый раз очищать <div>, чтобы не было здоровенной простыни из ошибок
		var htmlNode = YAHOO.util.Dom.get(form.errorContainer);
		htmlNode.innerHTML = "";

		var formData = form.getFormData();
		var canDelegateAll = "true" === formData["prop_lecm-d8n_delegation-opts-can-delegate-all"];
		// var isValid;
		/*
		if (canDelegateAll) {
			if (field.value) {
				isValid = true;
			} else {
				isValid = false;
			}
		} else {
			isValid = true;
		}
		*/
		var isValid = !canDelegateAll || field.value;

		if (!isValid) {
			form.addError(message, field);
		}
		return isValid;
	}
})();