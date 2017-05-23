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
    if (args && args.repeating) {
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

LogicECM.constraints.isUnique = function(field, args, event, form, silent, message) {
    var valid = true;

    var type = args != null ? args.typeName : null;
    var nodeRef = args != null ? args.nodeRef : null;
    var property = args != null ? args.propertyName : field.name.replace("prop_", "").replace("_", ":");
    var messageId = args != null ? args.messageId : 'lecm.element.duplicate.name';
    var checkInArchive = args != null ? args.checkInArchive : false;

    var errorMessage = message;

    if (field.value.length > 0) {
        var validationUrl = Alfresco.constants.PROXY_URI_RELATIVE + "lecm/base/validation/uniqueness?newValue=" + field.value + "&propertyName=" + property;
        if (nodeRef) {
            validationUrl += "&nodeRef=" + nodeRef;
        }
        if (type) {
            validationUrl += "&typeName=" + type;
        }
        jQuery.ajax({
            url: validationUrl,
            type: "GET",
            timeout: 30000,
            async: false,
            dataType: "json",
            contentType: "application/json",
            processData: false,
            success: function(result) {
                if (result != null) {
                    valid = result.isUnique && (!checkInArchive || result.isUniqueInArchive);
                    if (!valid) {
                        if (result.isUnique && !result.isUniqueInArchive) {
                            errorMessage = Alfresco.util.message(messageId + ".archive");
                        } else {
                            errorMessage = Alfresco.util.message(messageId);
                        }
                    }
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                errorMessage = Alfresco.util.message('lecm.element.error.validation');
                valid = false;
            }
        });
    } else {
        valid = true;
    }

    this.message = errorMessage; // подменяем сообщение

    return valid;
};


