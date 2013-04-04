if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Delegation = LogicECM.module.Delegation || {};

LogicECM.module.Delegation.Validator = LogicECM.module.Delegation.Validator || {};

LogicECM.module.Delegation.Validator.Utils = LogicECM.module.Delegation.Validator.Utils || {};

(function () {
	"use strict";
	LogicECM.module.Delegation.Validator.CanTransferRightsValidator = function (field, args,  event, form, silent, message) {

		var valid = true;
		// ID элемента, куда выплевывать сообщение об ошибке
		form.setErrorContainer("error-message-container");
		// Каждый раз очищать <div>, чтобы не было здоровенной простыни из ошибок
		var htmlNode = YAHOO.util.Dom.get(form.errorContainer);
		htmlNode.innerHTML = "";

		var value = "true" === field.value;

		if (value) {

			var datagrid = LogicECM.module.Base.Util.findComponentByBubblingLabel ("LogicECM.module.Base.DataGrid", "procuracy-datagrid");
			var tableRows = datagrid.widgets.dataTable.getRecordSet().getRecords();
			// Перебираем все строки датагрида
			for (var j = 0; j < tableRows.length; j++) {
				var tableRow = tableRows[j].getData("itemData");
				var rowValue = tableRow["prop_lecm-d8n_procuracy-can-transfer-rights"].value;
				if (rowValue) {
					valid = false;
					break;
				}
			}
		} else { //если галка снята то всегда true
			valid = true;
		}

		//Ругнуться, что дата неправильная
		if (!valid) {
			form.addError(message, field);
		}

		return valid;
	}

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