/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Retrieves the value of the given named argument from the URL arguments
 *
 * @method getArgument
 * @param argName The name of the argument to locate
 * @param defValue The default value to use if the argument could not be found
 * @return The value or null if not found
 */
function getArgument(argName, defValue)
{
	var result = args[argName];

	// if we don't have a result and a default has been defined, return that instead
	if (result === null && typeof defValue !== "undefined")
	{
		result = defValue;
	}

	return result;
}

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

/**
 * Returns the list of fields configured to be visible for the
 * given mode. If this method returns null or an empty list the
 * component should attempt to display ALL known data for the item,
 * unless there are fields configured to be hidden.
 *
 * @method getVisibleFields
 * @param mode The mode the form is rendering, 'view', 'edit' or 'create'
 * @param formConfig The form configuration, maybe null
 * @return Array of field names or null
 */
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

/**
 * Creates an Object to represent the body of the POST request
 * to send to the form service.
 *
 * @method createPostBody
 * @param itemKind The kind of item
 * @param itemId The id of the item
 * @param visibleFields List of fields to get data for
 * @param formConfig The form configuration object
 * @return Object representing the POST body
 */
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
			var indexOf = fieldId.indexOf("|");
			var parsedFieldsId = indexOf == -1 ? fieldId : fieldId.substring(0, indexOf);
			postBodyFields.push(parsedFieldsId);
			if (formConfig.isFieldForced(fieldId))
			{
				postBodyForcedFields.push(parsedFieldsId);
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

/**
 * Main entrypoint for component webscript logic
 *
 * @method main
 */
function main()
{
	var itemType = getArgument("itemType"),
		formId = getArgument("formId", "datagrid"),
		columns = [],
		columnDefs = [];

	if (itemType !== null && itemType.length > 0)
	{
		// get the config for the form
		var formConfig = getFormConfig(itemType, formId);

		// может содержать форматную строку для колонки послке "|"
		var visibleFields = getVisibleFields("view", formConfig);
		var substituteStrings = [];
		for (var i = 0; i < visibleFields.length; i++) {
			var vf = visibleFields[i];
			var indexOf = vf.indexOf("|");
			if (indexOf != -1) {
				substituteStrings[vf.substring(0, indexOf)] = vf.substring(indexOf + 1);
			}
		}
		// build the JSON object to send to the server
		var postBody = createPostBody("type", itemType, visibleFields, formConfig);

		// make remote call to service
		var connector = remote.connect("alfresco");
		var json = connector.post("/api/formdefinitions", jsonUtils.toJSONString(postBody), "application/json");

		if (logger.isLoggingEnabled())
		{
			logger.log("json = " + json);
		}

		if (json.status == 401)
		{
			status.setCode(json.status, "Not authenticated");
			return;
		}
		else
		{
			var formModel = eval('(' + json + ')');

			// if we got a successful response attempt to render the form
			if (json.status == 200)
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
	}

	// pass form ui model to FTL
	model.columns = [];
	for (var k = 0; k < visibleFields.length; k++) {
		var obj = visibleFields[k];
		if (obj.indexOf("|") != -1) {
			obj = obj.substring(0, obj.indexOf("|"));
		}
		var colDef = columnDefs[obj];
		if (colDef == null) {
			colDef = {
				type:"",
				name:obj,
				label:"",
				dataType:"",
				sortable: false
			}
		}
		if (substituteStrings[obj] != null) {
			colDef.nameSubstituteString = substituteStrings[obj];
		}
		var label = null;
		var formField = formConfig.fields[obj];
		if (formField != null) {
			if (formField.labelId != null && formField.labelId != "") {
				label = msg.get(formField.labelId);
			}else if (formField.label != null && formField.label != "") {
				label = formField.label;
			}
		}
		if (label != null) {
			colDef.label = label;
		}
		model.columns.push(colDef);
	}
}

main();
