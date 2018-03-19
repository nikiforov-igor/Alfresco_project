if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Contracts = LogicECM.module.Contracts || {};

LogicECM.module.Contracts.currencyValidation =
	function Contracts_currencyValidation(field, args,  event, form, silent, message) {
		var totalAmount = field.form["prop_lecm-contract_totalAmount"];
		var currency = field.form["assoc_lecm-contract_currency-assoc"];

		if (totalAmount != null && currency != null && totalAmount.value.length > 0) {
			var totalAmountFloat = parseFloat(totalAmount.value);
			if (isNaN(totalAmountFloat)) {
				return false;
			} else {
				return currency.value.length > 0;
			}
		}
		return true;
	};

LogicECM.module.Contracts.stageDateRangeValidator =
	function stageDateRangeValidator(field, args,  event, form, silent, message) {
		var startDateField = field.form["prop_lecm-contract-table-structure_start-date"];
		var endDateField = field.form["prop_lecm-contract-table-structure_end-date"];

		if (startDateField && startDateField.value && endDateField && endDateField.value) {
			var startDate = Alfresco.util.fromISO8601(startDateField.value);
			var endDate =  Alfresco.util.fromISO8601(endDateField.value);
			return startDate.getTime() <= endDate.getTime();
		}
		return true;
	};
