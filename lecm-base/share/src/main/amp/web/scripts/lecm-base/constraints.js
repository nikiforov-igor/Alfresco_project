if (typeof LogicECM == "undefined" || !LogicECM) {
    LogicECM = {};
}

LogicECM.constraints = LogicECM.constraints || {};

LogicECM.constraints.notMandatory = function(field, args, event, form, silent, message) {
    return true;
};

LogicECM.constraints.isNumber = function number(field, args, event, form) {
    if (Alfresco.logger.isDebugEnabled())
        Alfresco.logger.debug("Validating field '" + field.id + "' is a number");

    var repeating = false;

    var numberExp = /[-]?\d+(\.\d+|,\d+)?/ig;

    // determine if field has repeating values
    if (args !== null && args.repeating) {
        repeating = true;
    }

    var valid = true;
    if (repeating) {
        // as it's repeating there could be multiple comma separated values
        var values = field.value.split(",");
        for (var i = 0; i < values.length; i++) {
            valid = !isNaN(parseFloat(+values[i])) && isFinite(+values[i]);

            if (valid) {
                var test = values[i].match(numberExp);
                valid = (test != null && test[0] == values[i]);
                if (!valid) {
                    break;
                }
            } else {
                break;
            }
        }
    }
    else {
        valid = !isNaN(parseFloat(+field.value)) && isFinite(+field.value);

        if (valid) {
            var test = field.value.match(numberExp);
            valid = (test != null && test[0] == field.value);
        }
    }

    return valid;
};


