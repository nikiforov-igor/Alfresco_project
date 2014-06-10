if (typeof LogicECM == "undefined" || !LogicECM) {
	var LogicECM = {};
}

LogicECM.module = LogicECM.module || {};

LogicECM.module.currentDateLimitValidation =
	function (field, args,  event, form, silent, message) {
		var valid = false;
		var fieldValue = field.value;
		if (fieldValue != null && fieldValue.length > 0) {
			var fieldDate = new Date(fieldValue);
			var today = new Date();
			today.setHours(0, 0, 0, 0);

			valid = fieldDate >= today;

			if (valid) {
				YAHOO.util.Dom.removeClass(field.id + "cntrl-date", "invalid");
			} else {
				YAHOO.util.Dom.addClass(field.id + "-cntrl-date", "invalid");
			}
		}
		return valid;
	};
