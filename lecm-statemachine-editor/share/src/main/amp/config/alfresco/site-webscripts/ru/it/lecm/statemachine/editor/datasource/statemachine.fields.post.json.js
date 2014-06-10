/**
 * Finds the configuration for the given item id, if
 * there isn't any configuration for the item null is
 * returned.
 *
 * @method getFormConfig
 * @param itemId The id of the item to retrieve for config for
 * @param formId The id of the specific form to lookup or null
 *               to get the default form
 * @return Object representing the configuration or null
 */
function getFormConfig(itemId, formId)
{
    var formConfig = null;

    // query for configuration for item
    var nodeConfig = config.scoped[itemId];

    if (nodeConfig !== null)
    {
        // get the forms configuration
        var formsConfig = nodeConfig.forms;

        if (formsConfig !== null)
        {
            if (formId !== null && formId.length > 0)
            {
                // look up the specific form
                formConfig = formsConfig.getForm(formId);
            }

            // drop back to default form if formId config missing
            if (formConfig === null)
            {
                // look up the default form
                formConfig = formsConfig.defaultForm;
            }
        }
    }

    return formConfig;
}

function getVisibleFields(mode, formConfig)
{
    var visibleFields = null;

    if (formConfig !== null)
    {
        // get visible fields for the current mode
        switch (mode)
        {
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

    if (logger.isLoggingEnabled())
    {
        var listOfVisibleFields = visibleFields;
        if (visibleFields !== null)
        {
            listOfVisibleFields = "[" + visibleFields.join(",") + "]";
        }
        logger.log("Fields configured to be visible for " + mode + " mode = " + listOfVisibleFields);
    }

    return visibleFields;
}

function createPostBody(itemKind, itemId, visibleFields, formConfig)
{
    var postBody = {};

    postBody.itemKind = itemKind;
    postBody.itemId = itemId.replace(":/", "");

    if (visibleFields !== null)
    {
        // create list of fields to show and a list of
        // those fields to 'force'
        var postBodyFields = [];
        var postBodyForcedFields = [];
        var fieldId = null;
        for (var f = 0; f < visibleFields.length; f++)
        {
            fieldId = visibleFields[f];
            postBodyFields.push(fieldId);
            if (formConfig.isFieldForced(fieldId))
            {
                postBodyForcedFields.push(fieldId);
            }
        }

        postBody.fields = postBodyFields;
        if (postBodyForcedFields.length > 0)
        {
            postBody.force = postBodyForcedFields;
        }
    }

    if (logger.isLoggingEnabled())
    {
        logger.log("postBody = " + jsonUtils.toJSONString(postBody));
    }

    return postBody;
}

function main() {
    var params = {};
    if (json != null && json.has("params")) {
        var pars = json.get("params");

        var nodeRef = pars.get("parent");
        var url = "/api/metadata?nodeRef=" + nodeRef + "&shortQNames";
        var node = eval('(' + remote.connect("alfresco").get(url) + ')');

        var modelName = node.properties["cm:name"].replace("_", ":");

        var formConfig = getFormConfig(modelName, "statemachine-editable-fields");

        var fields = [];
        // pass form ui model to FTL
        var visibleFields = getVisibleFields("edit", formConfig);

        var postBody = createPostBody("type", modelName, visibleFields, formConfig);
        var columnDefs = [];
        // make remote call to service
        var connector = remote.connect("alfresco");
        var jsonDefs = connector.post("/api/formdefinitions", jsonUtils.toJSONString(postBody), "application/json");

        if (jsonDefs.status == 401)
        {
            status.setCode(jsonDefs.status, "Not authenticated");
            return;
        }
        else
        {
            var formModel = eval('(' + jsonDefs + ')');
            // if we got a successful response attempt to render the form
            if (jsonDefs.status == 200)
            {
                columns = formModel.data.definition.fields;
                for (var j = 0; j < columns.length; j++) {
                    var col = columns[j];
                    columnDefs[col.name] = col;
                }
            }
            else
            {
                model.error = formModel.message;
            }
        }

        for each (var field in visibleFields) {
            fields.push(field);
        }

        var req = {
            nodeRef: nodeRef,
            fields: fields
        };

        var url = "/lecm/statemachine/editor/datagrid/fields";
        var fieldStatuses = eval('(' + remote.connect("alfresco").post(url, jsonUtils.toJSONString(req), "application/json") + ')');

        var result = [];
        for each (var fieldStatus in fieldStatuses) {
            var fields =  [];
            var colDef = columnDefs[fieldStatus.field];

            if (colDef == null) {   //поле фиктивное, создаем колонку
                colDef = {
                    type:"",
                    name: fieldStatus.field,
                    label:"",
                    dataType:"",
                    sortable: false
                }
            }

            var formField = formConfig.fields[fieldStatus.field];
            var label = colDef.label;
            if (formField != null) {
                //забираем подпись из конфига
                if (formField.labelId != null && formField.labelId != "") {
                    label = msg.get(formField.labelId);
                }else if (formField.label != null && formField.label != "") {
                    label = formField.label;
                }
            }
            fields.push({
                fieldName: "prop_form_field",
                value: fieldStatus.field,
                displayValue: label == null || label == "" ? fieldStatus.field : label
            })
            for each (var status in fieldStatus.statuses) {
                fields.push({
                    fieldName: "prop_" + status.status,
                    displayValue: status.editableField,
                    value: status.fieldNodeRef
                })
            }
            result.push(fields);
        }
        result.sort(function sortResult(item1, item2) {
            if (item1[0].displayValue > item2[0].displayValue) {
                return 1;
            } else if (item1[0].displayValue < item2[0].displayValue) {
                return -1;
            } else {
                return 0;
            }
        });
        model.result = result;
    }
}

main();
