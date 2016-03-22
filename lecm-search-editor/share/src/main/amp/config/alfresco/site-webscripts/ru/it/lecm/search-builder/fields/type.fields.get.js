var formsConfig = null;

function getArgument(argName, defValue) {
    var result = args[argName];

    if (result === null && typeof defValue !== "undefined") {
        result = defValue;
    }

    return result;
}

function getFormConfig(itemId, formId, useDefaultForm) {
    var formConfig = null;
    // query for configuration for item
    var nodeConfig = config.scoped[itemId];
    if (nodeConfig !== null) {
        // get the forms configuration
        formsConfig = nodeConfig.forms;

        if (formsConfig !== null) {
            if (formId !== null && formId.length > 0) {
                // look up the specific form
                formConfig = formsConfig.getForm(formId);
            }

            if (formConfig === null && useDefaultForm) {
                // look up the default form
                formConfig = formsConfig.defaultForm;
            }
        }
    }

    return formConfig;
}

function getVisibleFields(mode, formConfig) {
    var visibleFields = null;

    if (formConfig !== null) {
        // get visible fields for the current mode
        switch (mode) {
            case "view":
                visibleFields = formConfig.visibleViewFieldNames;
                break;
            case "edit":
                visibleFields = formConfig.visibleEditFieldNames;
                break;
            case "create":
                visibleFields = formConfig.visibleCreateFieldNames;
                break;
            default:
                visibleFields = formConfig.visibleViewFieldNames;
                break;
        }
    }
    return visibleFields;
}

function createPostBody(itemKind, itemId, visibleFields, formConfig) {
    var postBody = {};

    postBody.itemKind = itemKind;
    postBody.itemId = itemId.replace(":/", "");

    if (visibleFields !== null) {
        var postBodyFields = [];
        var postBodyForcedFields = [];
        var fieldId = null;
        for (var f = 0; f < visibleFields.length; f++) {
            fieldId = visibleFields[f];
            postBodyFields.push(fieldId);
            if (formConfig.isFieldForced(fieldId)) {
                postBodyForcedFields.push(fieldId);
            }
        }

        postBody.fields = postBodyFields;
        if (postBodyForcedFields.length > 0) {
            postBody.force = postBodyForcedFields;
        }
    }

    return postBody;
}

function isDataTypeNumber(dataType)
{
    if ("int" === dataType || "long" === dataType || "double" === dataType || "float" === dataType)
    {
        return true;
    }

    return false;
}

function processFieldConstraints(fieldDefinition, fieldConfig) {
    var constraints = [];
    if (fieldDefinition.endpointMandatory) {
        constraints.push(generateConstraintModelById(fieldDefinition, 'MANDATORY'));
    }

    if (fieldConfig) {
        var constraintDefinitionMap = fieldConfig.getConstraintDefinitionMap();
        if (constraintDefinitionMap) {
            for (var i = 0; i < constraintDefinitionMap.length; i++) {
                var constraintHandler = constraintDefinitionMap[i];
                constraints.push(generateConstraintModel(fieldDefinition, constraintHandler));
            }
        }
    }

    if (fieldDefinition.constraints) {
        for (i = 0; i < fieldDefinition.constraints.length; i++) {
            constraints.push(generateConstraintModelById(fieldDefinition, fieldDefinition.constraints[i].type))
        }
    }

    if (isDataTypeNumber(fieldDefinition.dataType)) {
        constraints.push(generateConstraintModelById(fieldDefinition, 'NUMBER'));
    }



    return constraints;

}

function generateConstraintModelById(fieldDefinition, constraintId) {
    var constraintHandlers = formsConfig.getConstraintHandlers();
    var constraintHandler = constraintHandlers.items[constraintId];

    return generateConstraintModel(fieldDefinition, constraintHandler);
}

function generateConstraintModel(fieldDefinition, constraintHandler) {
    var constraint = {};

    if (constraintHandler) {
        constraint.fieldId = fieldDefinition.dataKeyName;
        constraint.handler = constraintHandler.validationHandler;
        constraint.event = constraintHandler.event;
        if (constraintHandler.messageId) {
            constraint.message = constraintHandler.messageId;
        } else if (constraintHandler.message) {
            constraint.message = constraintHandler.message;
        } else {
            constraint.message = constraintHandler.validationHandler + ".message";
        }
    }

    return constraint;
}

function main() {
    var itemType = getArgument("itemType"),
        formId = getArgument("formId", "search-editor-fields"),
		useDefaultForm = getArgument('useDefaultForm', 'false') == 'true',
        fields = [],
        columnDefs = [];

    if (itemType !== null && itemType.length > 0) {
        // get the config for the form
        var formConfig = getFormConfig(itemType, formId, useDefaultForm);

        // может содержать форматную строку для колонки послке "|"
        var visibleFields = getVisibleFields("view", formConfig);
        // build the JSON object to send to the server
        var postBody = createPostBody("type", itemType, visibleFields, formConfig);

        // make remote call to service
        var connector = remote.connect("alfresco");
        var json = connector.post("/api/formdefinitions", jsonUtils.toJSONString(postBody), "application/json");

        if (json.status == 401) {
            status.setCode(json.status, "Not authenticated");
            return;
        }
        else {
            var formModel = eval('(' + json + ')');

            // if we got a successful response attempt to render the form
            if (json.status == 200) {
                fields = formModel.data.definition.fields;
                for (var j = 0; j < fields.length; j++) {
                    var col = fields[j];
                    columnDefs[col.name] = col;
                }
            }
            else {
                model.error = formModel.message;
            }
        }
    }
    if (visibleFields != null) {
        // pass form ui model to FTL
        model.fields = [];
        //проходим все поля, включая фиктивные
        for (var k = 0; k < visibleFields.length; k++) {
            var obj = visibleFields[k];

            var colDef = columnDefs[obj];

            if (colDef == null) {   //поле фиктивное, создаем колонку
                colDef = {
                    type: "association",
                    name: obj,
                    label: "",
                    dataType: "association",
                    sortable: false
                }
            }

            var formField = formConfig.fields[obj];
            if (formField != null) {
                var label = null;
                //забираем подпись из конфига
                if (formField.labelId != null && formField.labelId != "") {
                    label = msg.get(formField.labelId);
                } else if (formField.label != null && formField.label != "") {
                    label = formField.label;
                }
                if (label != null) {
                    colDef.label = label;
                }
                if (formField.attributes) {
                    //забираем форматную строку
                    if (formField.attributes.substituteString != null && formField.attributes.substituteString != "") {
                        colDef.nameSubstituteString = formField.attributes.substituteString;
                    }
                }
                colDef.control = {};
                colDef.control.template = (formField.control !== null ? formField.control.template : '');
                colDef.control.params = [];
                if (formField.control !== null) {
                    var controlParams = formField.control.params;
                    for (var p = 0; p < controlParams.length; p++) {
                        var param = controlParams[p];
                        colDef.control.params.push(param);
                    }
                }
                colDef.constraints = processFieldConstraints(colDef, formField);
            }
            model.fields.push(colDef);
        }
    } else {
        model.fields = fields;
    }
}

main();
