if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Errands = LogicECM.module.Errands || {};

LogicECM.module.Errands.limitationDateValidation =
    function (field, args, event, form, silent, message) {
        if (field.form != null) {
            var radio = field.form["prop_lecm-errands_limitation-date-radio"];
            var days = field.form["prop_lecm-errands_limitation-date-days"];
            var limitationDate = field.form["prop_lecm-errands_limitation-date"];

            var radioValue = null;
            if (radio != null) {
                for (var i = 0; i < radio.length; i++) {
                    if (radio[i].checked == true) {
                        radioValue = radio[i].value;
                    }
                }
            }

            if (radioValue == "LIMITLESS") {
                return true;
            } else if (radioValue == "DAYS") {
                return days.value.length > 0;
            } else {
                return limitationDate != null && limitationDate.value.length > 0
            }
        }
        return true;
    };
