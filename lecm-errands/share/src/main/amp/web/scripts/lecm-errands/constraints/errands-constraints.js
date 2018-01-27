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
            } else {
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


LogicECM.module.Errands.commonPeriodValidation =
    function (field, args, event, form, silent, message, props) {
        var periodicallyEl = field.form[props.periodicallyProp];
        if (periodicallyEl && periodicallyEl.value == "true") {
            var startDate = field.form[props.startProp];
            var endRadio = field.form[props.radioProp];
            endRadio = getCheckedRadio(endRadio);
            var endDateEl = field.form[props.endProp];
            var duringEl = field.form[props.duringProp];
            var reiterationCountEl = field.form[props.repeatCountProp];

            var isValid = startDate && startDate.value;

            if (endRadio) {
                if (field.name == "prop_lecm-errands_period-start") {
                    return isValid;
                } else if (isValid && field.name == props.endProp && endRadio.value == "DATERANGE") {
                    return endDateEl && endDateEl.value;
                } else if (field.name == props.duringProp && endRadio.value == "DURING") {
                    return duringEl && duringEl.value;
                } else if (field.name == props.repeatCountProp && endRadio.value == "REPEAT_COUNT") {
                    return reiterationCountEl && reiterationCountEl.value;
                }
            }
        }
        return true;
    };

LogicECM.module.Errands.periodValidation =
    function (field, args, event, form, silent, message) {
        var props = {
            startProp: "prop_lecm-errands_period-start",
            endProp: "prop_lecm-errands_period-end",
            radioProp: "prop_lecm-errands_periodically-radio",
            duringProp: "prop_lecm-errands_period-during",
            repeatCountProp: "prop_lecm-errands_reiteration-count",
            periodicallyProp: "prop_lecm-errands_periodically"
        };
        return LogicECM.module.Errands.commonPeriodValidation(field, args, event, form, silent, message, props);
    };


LogicECM.module.Errands.createErrandWFPeriodValidation =
    function (field, args, event, form, silent, message) {
        var props = {
            startProp: "prop_lecmErrandWf_periodStart",
            endProp: "prop_lecmErrandWf_periodEnd",
            radioProp: "prop_lecmErrandWf_periodicallyRadio",
            periodicallyProp: "prop_lecmErrandWf_periodically",
            duringProp: "prop_lecmErrandWf_periodDuring",
            repeatCountProp: "prop_lecmErrandWf_reiterationCount"
        };

        return LogicECM.module.Errands.commonPeriodValidation(field, args, event, form, silent, message, props);
    };

LogicECM.module.Errands.commonPeriodEndDateValidation = function (field, args, event, form, silent, message, props) {
    var periodicallyEl = field.form[props.periodicallyProp];
    if (periodicallyEl && periodicallyEl.value == "true") {
        var startDate = field.form[props.startProp];
        var endRadio = field.form[props.radioProp];
        endRadio = getCheckedRadio(endRadio);
        var endDateEl = field.form[props.endProp];

        var isValid = startDate && startDate.value;
        if (!isValid || !endDateEl || !endDateEl.value) {
            return true;
        }
        if (endRadio && endRadio.value == "DATERANGE") {
            return isValid && endDateEl && endDateEl.value && new Date(endDateEl.value) >= new Date(startDate.value);
        }
    }
    return true;
};


LogicECM.module.Errands.periodEndDateValidation = function (field, args, event, form, silent, message) {
    var props = {
        startProp: "prop_lecm-errands_period-start",
        endProp: "prop_lecm-errands_period-end",
        radioProp: "prop_lecm-errands_periodically-radio",
        duringProp: "prop_lecm-errands_period-during",
        repeatCountProp: "prop_lecm-errands_reiteration-count",
        periodicallyProp: "prop_lecm-errands_periodically"
    };
    return LogicECM.module.Errands.commonPeriodEndDateValidation(field, args, event, form, silent, message, props);
};

LogicECM.module.Errands.createErrandWFPeriodEndDateValidation = function (field, args, event, form, silent, message) {
    var props = {
        startProp: "prop_lecmErrandWF_periodStart",
        endProp: "prop_lecmErrandWF_periodEnd",
        radioProp: "prop_lecmErrandWF_periodicallyRadio",
        duringProp: "prop_lecmErrandWF_periodDuring",
        repeatCountProp: "prop_lecmErrandWF_reiterationCount",
        periodicallyProp: "prop_lecmErrandWF_periodically"
    };
    return LogicECM.module.Errands.commonPeriodEndDateValidation(field, args, event, form, silent, message, props);
};

LogicECM.module.Errands.positiveNumberValidation = function (field, args, event, form, silent, message) {
    var pattern = /^[1-9][0-9]*$/;
    return pattern.test(field.value);
};

function getCheckedRadio(radio) {
    if (radio instanceof HTMLCollection) {
        for (var i = 0; i < radio.length; i++) {
            var radioInput = radio[i];
            if (radioInput.checked) {
                return radioInput;
            }
        }
    }
    return radio;
}