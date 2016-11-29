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
