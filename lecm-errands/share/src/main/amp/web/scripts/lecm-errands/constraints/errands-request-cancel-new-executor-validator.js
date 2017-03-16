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
				var formId = form.formId.replace("-form", "");
				var isChangeExecutor = false;

				if (result != null && result.value == "CHANGE_EXECUTOR") {
					isChangeExecutor = true;
					LogicECM.module.Base.Util.readonlyControl(formId, "lecmErrandWf:requestCancelTaskNewExecutor", false);
				} else {
					isChangeExecutor = false;
					if (newExecutor.value != '') {
						newExecutor.value = '';
						LogicECM.module.Base.Util.reInitializeControl(formId, 'lecmErrandWf:requestCancelTaskNewExecutor', {
							currentValue:""
						});
					}
					LogicECM.module.Base.Util.readonlyControl(formId, "lecmErrandWf:requestCancelTaskNewExecutor", true);
				}

				return !isChangeExecutor || newExecutor.value.length > 0;
			}
		}
		return true;
	};
