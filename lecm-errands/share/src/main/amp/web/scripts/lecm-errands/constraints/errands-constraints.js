if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Errands = LogicECM.module.Errands || {};

LogicECM.module.Errands.limitationDateValidation =
    function (field, args, event, form, silent, message) {
        if (field.form) {
            var radio = field.form["prop_lecm-errands_limitation-date-radio"];
            var days = field.form["prop_lecm-errands_limitation-date-days"];
            var limitationDate = field.form["prop_lecm-errands_limitation-date"];

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
            } else if (radioValue == "DAYS") {
                return (days.value.length > 0) || (field.name != "prop_lecm-errands_limitation-date-days");
            } else {
                return (limitationDate && limitationDate.value.length) || (field.name != "prop_lecm-errands_limitation-date");
            }
        }
        return true;
    };
LogicECM.module.Errands.WFChangeDueDateValidation =
    function (field, args, event, form, silent, message) {
        if (field.name != "prop_lecmErrandWf_changeDueDateNewDueDate") {
            return true;
        }
        if (field.form) {
            var radio = field.form["prop_lecmErrandWf_changeDueDateNewDueDateRadio"];
            var limitationDate = field.form["prop_lecmErrandWf_changeDueDateNewDueDate"];
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
            }else{
                return (limitationDate && limitationDate.value.length);
            }
        }
        return true;
    };
/**
 * @return {boolean}
 */
LogicECM.module.Errands.CancelReasonLengthValidation =
    function (field, args, event, form, silent, message) {
        return field.value.length > 0 && field.value.length <= 200;
    };
/**
 * @return {boolean}
 */
LogicECM.module.Errands.RequestTaskCancelReasonLengthValidation =
    function (field, args, event, form, silent, message) {
        if (field.form["prop_lecmErrandWf_requestCancelTask_1Result"].value == "CANCEL_ERRAND") {
            return field.value.length > 0 && field.value.length < 200;
        }
        return true;
    };