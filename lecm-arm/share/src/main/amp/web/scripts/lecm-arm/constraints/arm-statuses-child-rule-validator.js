if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.ARM = LogicECM.module.ARM || {};

LogicECM.module.ARM.statusesChildRuleValidation =
	function (field, args,  event, form, silent, message) {
		if (field.form != null) {
			var statusesTextField = field.form["prop_lecm-arm_selected-statuses"];
			if (statusesTextField != null) {
				statusesTextField.disabled = field.value != "SELECTED" && field.value != "EXCEPT_SELECTED";
			}
		}
		return true;
	};
