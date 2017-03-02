if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.ORD = LogicECM.module.ORD || {};

LogicECM.module.ORD.commonLimitationDateValidation =
    function (field, args, event, form, silent, message, props) {
        if (field.form) {
            var radio = field.form["prop_lecm-ord-table-structure_limitation-date-radio"];
            var days = field.form["prop_lecm-ord-table-structure_limitation-date-days"];
            var limitationDate = field.form["prop_lecm-ord-table-structure_execution-date"];

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
                return (days.value.length > 0) || (field.name != "prop_lecm-ord-table-structure_limitation-date-days");
            } else {
                return (limitationDate && limitationDate.value.length) || (field.name != "prop_lecm-ord-table-structure_execution-date");
            }
        }
        return true;
    };

LogicECM.module.ORD.itemControllerValidation =
    function (field, args, event, form, silent, message, props) {
        if (field.form) {
            var controllerField = field.form["assoc_lecm-ord-table-structure_controller-assoc"];
            var reportRequired = field.form["prop_lecm-ord-table-structure_report-required"];
            if (reportRequired.value == "true" && field.name != "prop_lecm-ord-table-structure_report-required") {
                return controllerField.value.length > 0;
            }
        }
        return true;
    };