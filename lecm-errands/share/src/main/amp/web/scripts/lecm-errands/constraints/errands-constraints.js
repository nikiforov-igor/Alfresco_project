if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.module = LogicECM.module || {};
LogicECM.module.Errands = LogicECM.module.Errands || {};

LogicECM.module.Errands.commonLimitationDateValidation =
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

LogicECM.module.Errands.limitationDateValidation =
    function (field, args, event, form, silent, message) {
        var props = {
            radioProp: "lecm-errands_limitation-date-radio",
            daysProp: "lecm-errands_limitation-date-days",
            dateProp: "lecm-errands_limitation-date"
        };
        return LogicECM.module.Errands.commonLimitationDateValidation(field, args, event, form, silent, message, props);
    };

LogicECM.module.Errands.createErrandWFLimitationDateValidation =
    function (field, args, event, form, silent, message) {
        var props = {
            radioProp: "lecmErrandWf_limitationDateRadio",
            daysProp: "lecmErrandWf_limitationDateDays",
            dateProp: "lecmErrandWf_limitationDate"
        };
        return LogicECM.module.Errands.commonLimitationDateValidation(field, args, event, form, silent, message, props);
    };


LogicECM.module.Errands.OValidation = function (field, args, event, form, silent, message) {
    args = {
        maxValue: 2147483647,
        minValue: 1
    };
    var isValid = Alfresco.forms.validation.numberRange(field, args, event, form, silent, message);

    this.message = LogicECM.module.Errands.OValidation.MessageHandler;

    this.args.fieldId = field.id;

    if (!this.args.messageHandlerBinded) {
        this.args.messageHandlerBinded = true;
        return form._runValidations(event, field.id, Alfresco.forms.Form.NOTIFICATION_LEVEL_CONTAINER);
    }

    return isValid;
};

LogicECM.module.Errands.OValidation.MessageHandler = function (args) {
    var fieldValue = "";
    if (args.fieldId) {
        var field = Dom.get(args.fieldId);
        if (field) {
            fieldValue = field.value;
        }
    }
    if (!/^0+$/.test(fieldValue)) {
        return Alfresco.util.message("Alfresco.forms.validation.numberRange.message");
    } else {
        return Alfresco.util.message("message.constraint.number.positive");
    }
};