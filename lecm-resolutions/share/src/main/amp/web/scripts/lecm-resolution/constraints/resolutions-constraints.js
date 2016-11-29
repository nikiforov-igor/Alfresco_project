if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Resolutions = LogicECM.module.Resolutions || {};

LogicECM.module.Resolutions.limitationDateValidation =
    function (field) {
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

LogicECM.module.Resolutions.controllerValidation =
    function (field) {
        if (field.form) {
            var control = field.form["prop_lecm-resolutions_control"];

            return (control && control.value == "false") || (field.value && field.value.length);
        }
        return true;
    };
LogicECM.module.Resolutions.ErrandsExecutionDateValidation =
    function (field) {
        if (YAHOO.util.Dom.hasClass(field, "execution-date-invalid")) {
            YAHOO.util.Dom.removeClass(field, "execution-date-invalid");
            return false;
        } else {
            return true;
        }
    };
