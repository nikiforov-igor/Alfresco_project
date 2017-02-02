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
        var dateField = Selector.query(".errands-wf-duedate-set-date .value-div input[type='hidden']", YAHOO.util.Dom.get(field.form.id), true);
        if (field.name != dateField.name) {
            return true;
        }
        if (field.form) {
            var radio = Selector.query(".errands-wf-duedate-set-radio .value-div input[type='radio']", YAHOO.util.Dom.get(field.form.id));
            var limitationDate = dateField;
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
