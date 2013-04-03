if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.Delegation = LogicECM.module.Delegation || {};

LogicECM.module.Delegation.Validator = LogicECM.module.Delegation.Validator || {};

LogicECM.module.Delegation.Validator.Utils = LogicECM.module.Delegation.Validator.Utils || {};

(function () {
	"use strict";
	LogicECM.module.Delegation.Validator.Utils.findDatagridByName = function (p_sName, bubblingLabel) {
		var components = [];
		var found = [];
		var bMatch, component;

		components = Alfresco.util.ComponentManager.list();

		for (var i = 0, j = components.length; i < j; i++) {
			component = components[i];
			bMatch = true;
			if (component['name'].search(p_sName) == -1 ) {
				bMatch = false;
			}
			if (bMatch) {
				found.push(component);
			}
		}
		if (bubblingLabel) {
			for (i = 0, j = found.length; i < j; i++) {
				component = found[i];
				if (typeof component == "object" && component.options.bubblingLabel) {
					if (component.options.bubblingLabel == bubblingLabel) {
						return component;
					}
				}
			}
		} else {
			return (typeof found[0] == "object" ? found[0] : null);
		}
		return null;
	};

	LogicECM.module.Delegation.Validator.CanTransferRightsValidator = function (field, args,  event, form, silent, message) {

		var valid = true;
		// ID элемента, куда выплевывать сообщение об ошибке
		form.setErrorContainer("error-message-container");
		// Каждый раз очищать <div>, чтобы не было здоровенной простыни из ошибок
		var htmlNode = YAHOO.util.Dom.get(form.errorContainer);
		htmlNode.innerHTML = "";

		var value = "true" == field.value;

		if (value) {

			var datagrid = LogicECM.module.Delegation.Validator.Utils.findDatagridByName ("LogicECM.module.Base.DataGrid", "procuracy-datagrid");
			tableRows = datagrid.widgets.dataTable.getRecordSet().getRecords();
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
})();