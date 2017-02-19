if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.ORD = LogicECM.module.ORD || {};

LogicECM.module.ORD.commonLimitationDateValidation =
    function (field, args, event, form, silent, message, props) {
        if (field.form) {
            var radio = field.form["prop_" + props.radioProp];
            var days = field.form["prop_" + props.daysProp];
            var limitationDate = field.form["prop_" + props.dateProp];

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
                return (days.value.length > 0) || (field.name != "prop_" + props.daysProp);
            } else {
                return (limitationDate && limitationDate.value.length) || (field.name != "prop_" + props.dateProp);
            }
        }
        return true;
    };

LogicECM.module.ORD.limitationDateValidation =
    function (field, args, event, form, silent, message) {
        var props = {
            radioProp: "lecm-ord-table-structure_item-limitation-date-radio",
            daysProp: "lecm-ord-table-structure_item-limitation-date-days",
            dateProp: "lecm-ord-table-structure_item-limitation-date"
        };
        return LogicECM.module.ORD.commonLimitationDateValidation(field, args, event, form, silent, message, props);
    };