if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Resolutions = LogicECM.module.Resolutions || {};

LogicECM.module.Resolutions.limitationDateValidation =
    function (field, args, event, form, silent, message) {
        if (field.form) {
            var radio = field.form["prop_lecm-resolutions_limitation-date-radio"];
            var days = field.form["prop_lecm-resolutions_limitation-date-days"];
            var limitationDate = field.form["prop_lecm-resolutions_limitation-date"];

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
                return (days.value.length > 0) || (field.name != "prop_lecm-resolutions_limitation-date-days");
            } else {
                return (limitationDate && limitationDate.value.length) || (field.name != "prop_lecm-resolutions_limitation-date");
            }
        }
        return true;
    };
