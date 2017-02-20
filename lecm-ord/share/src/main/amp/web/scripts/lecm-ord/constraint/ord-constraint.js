if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.ORD = LogicECM.module.ORD || {};

LogicECM.module.ORD.commonLimitationDateValidation =
    function (field, args, event, form, silent, message, props) {
        if (field.form) {
            var radio = field.form["prop_lecm-ord-table-structure_item-limitation-date-radio"];
            var days = field.form["prop_lecm-ord-table-structure_item-limitation-date-days"];
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
                return (days.value.length > 0) || (field.name != "prop_lecm-ord-table-structure_item-limitation-date-days");
            } else {
                return (limitationDate && limitationDate.value.length) || (field.name != "prop_lecm-ord-table-structure_execution-date");
            }
        }
        return true;
    };