if (typeof LogicECM == "undefined" || !LogicECM) {
	LogicECM = {};
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
LogicECM.module.Errands.requestChangeDueDateTask_1NewDateValidation =
	function (field, args, event, form, silent, message) {
		var result = field.form["prop_lecmErrandWf_requestDueDateChangeTask_1Result"];
		var isApprove = result && result.value == "APPROVED";
		if (isApprove) {
			if (field.name != "prop_lecmErrandWf_requestDueDateChangeTask_1NewDate") {
				return true;
			}
			if (field.form) {
				var radio = field.form["prop_lecmErrandWf_requestDueDateChangeTask_1NewDueDateRadio"];
				var limitationDate = field.form["prop_lecmErrandWf_requestDueDateChangeTask_1NewDate"];
				var radioValue = null;
				if (radio) {
					for (var i = 0; i < radio.length; i++) {
						if (radio[i].checked == true) {
							radioValue = radio[i].value;
						}
					}
				}
				if (radioValue == "LIMITLESS") {
					return true;
				} else {
					return (limitationDate && limitationDate.value.length);
				}
			}
		}
		return true;
	};
LogicECM.module.Errands.requestDueDateChangeTaskRejectReasonValidation =
	function (field, args, event, form, silent, message) {
		var result = field.form["prop_lecmErrandWf_requestDueDateChangeTask_1Result"];
		var isReject = result && result.value == "REJECTED";
		if (isReject) {
			var rejectReason = field.form["prop_lecmErrandWf_requestDueDateChangeTask_1RejectReason"];
			return rejectReason.value.length > 0;
		}
		return true;
	};