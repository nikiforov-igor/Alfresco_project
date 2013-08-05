if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Errands = LogicECM.module.Errands || {};

LogicECM.module.Errands.requestChangeDueDateTaskNewDateValidation =
	function (field, args,  event, form, silent, message) {
		if (field.form != null) {
			var result = field.form["prop_lecmErrandWf_requestDueDateChangeTaskResult"];
			var newDate = field.form["prop_lecmErrandWf_requestDueDateChangeTaskNewDate"];

			if (newDate != null) {
				var isApprove = result != null && result.value == "APPROVED";

				var dateInput = YAHOO.util.Dom.get(newDate.id + "-cntrl-date");
				if (dateInput != null) {
					dateInput.disabled = !isApprove;
				}

				return newDate.value.length > 0;
			}
		}
		return true;
	};
