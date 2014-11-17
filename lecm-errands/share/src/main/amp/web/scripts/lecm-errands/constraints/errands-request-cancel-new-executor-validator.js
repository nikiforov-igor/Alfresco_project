if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Errands = LogicECM.module.Errands || {};

LogicECM.module.Errands.requestCancelTaskNewExecutorValidation =
	function (field, args,  event, form, silent, message) {
		if (field.form != null) {
			var result = field.form["prop_lecmErrandWf_requestCancelTaskResult"];
			var newExecutor = field.form["assoc_lecmErrandWf_requestCancelTaskNewExecutor"];

			if (newExecutor != null) {
				var isChangeExecutor = false;
				if (result != null && result.value == "CHANGE_EXECUTOR") {
					isChangeExecutor = true;
				}

				var newExecutorInput = field.form["assoc_lecmErrandWf_requestCancelTaskNewExecutor-autocomplete-input"];
				if (newExecutorInput != null) {
					newExecutorInput.disabled = !isChangeExecutor;
				}
				var newExecutorButton = field.form["assoc_lecmErrandWf_requestCancelTaskNewExecutor-tree-picker-button"];
				if (newExecutorButton != null) {
					newExecutorButton.disabled = !isChangeExecutor;
				}

				return !isChangeExecutor || newExecutor.value.length > 0;
			}
		}
		return true;
	};
