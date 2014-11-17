if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.RegNumbers = LogicECM.module.RegNumbers || {};


LogicECM.module.RegNumbers.templateValidation = function(field, args, event, form, silent, message) {
	var errorContainer, valid = true, errorMessage = "";

	if (!LogicECM.module.RegNumbers.errorContainerID) {
		errorContainer = createErrorContainer();
	} else {
		errorContainer = YAHOO.util.Dom.get(LogicECM.module.RegNumbers.errorContainerID);
		if (!errorContainer) {
			errorContainer = createErrorContainer();
		}
	}

	// ID элемента, куда выплевывать сообщение об ошибке
	form.setErrorContainer(LogicECM.module.RegNumbers.errorContainerID);

	// Каждый раз очищать <div>, чтобы не было здоровенной простыни из ошибок
	errorContainer.innerHTML = "";

	optsObj = {
		template: field.value,
		verbose: false
	};
	if (field.value.length > 0) {
		// Yahoo UI не умеет синхронный (блокирующий) AJAX. Придется использовать jQuery
		jQuery.ajax({
			url: Alfresco.constants.PROXY_URI_RELATIVE + "lecm/regNumbers/validateTemplate",
			type: "POST",
			timeout: 30000, // 30 секунд таймаута хватит всем!
			async: false, // ничего не делаем, пока не отработал запрос
			dataType: "json",
			contentType: "application/json",
			data: YAHOO.lang.JSON.stringify(optsObj), // jQuery странно кодирует данные. пусть YUI эаймеся преобразованием в JSON
			processData: false, // данные не трогать, не кодировать вообще
			success: function(result, textStatus, jqXHR) {
				if (result != null && !result.isValid) {
					errorMessage += Alfresco.component.Base.prototype.msg("message.error.regnumbers.template.validation")
							+ "<br>" + result.errorReason;

					valid = false;
				}
			},
			error: function(jqXHR, textStatus, errorThrown) {
				Alfresco.util.PopupManager.displayMessage({
					text: "ERROR: can not perform field validation"
				});
				valid = false;
			}
		});
	} else {
		valid = false;
	}

	// Ругнуться, что с шаблоном номера что-то плохо
	if (!valid) {
		errorContainer.innerHTML = "";
		form.addError(errorMessage, field);
	}

	return valid;

	function createErrorContainer() {
		var ec = document.createElement('div');
		LogicECM.module.RegNumbers.errorContainerID = YAHOO.util.Dom.generateId(ec);
		field.parentNode.appendChild(ec);
		ec.style.whiteSpace = "normal";
		return ec;
	}
};
