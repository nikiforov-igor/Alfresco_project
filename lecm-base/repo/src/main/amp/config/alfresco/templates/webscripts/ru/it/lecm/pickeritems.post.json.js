<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/repository/forms/pickerresults.lib.js">

function main()
{
   var count = 0,
      items = [],
      results = [];

   // extract mandatory data from request body
   if (!json.has("items"))
   {
       status.setCode(status.STATUS_BAD_REQUEST, "items parameter is not present");
       return;
   }
   
   // convert the JSONArray object into a native JavaScript array
   var jsonItems = json.get("items"),
      itemValueType = "nodeRef",
      itemValueTypeHint = "",
      itemNameSubstituteString = "{cm:name}",
      substituteParent = "none",
      selectedItemsNameSubstituteString = "{cm:name}",
      numItems = jsonItems.length(),
      additionalProperties = json.has('additionalProperties') ? json.get('additionalProperties') : null,
      item, result;

    if (additionalProperties != null && additionalProperties != "none") {
        additionalProperties = additionalProperties.split(',');
    }
   if (json.has("itemValueType")) {
      var jsonValueTypes = json.get("itemValueType").split(";");
      itemValueType = jsonValueTypes[0];
      itemValueTypeHint = (jsonValueTypes.length > 1) ? jsonValueTypes[1] : "";
   }
   if (json.has("itemNameSubstituteString")) {
       itemNameSubstituteString = json.get("itemNameSubstituteString");
   }
    if (json.has("substituteParent")) {
        substituteParent = "" + json.get("substituteParent");
    }
	if (json.has("selectedItemsNameSubstituteString")) {
		selectedItemsNameSubstituteString = json.get("selectedItemsNameSubstituteString");
	} else {
		selectedItemsNameSubstituteString = itemNameSubstituteString;
	}

   for (count = 0; count < numItems; count++)
   {
      item = jsonItems.get(count);
      if (item != "")
      {
         result = null;
         if (itemValueType == "nodeRef")
         {
            result = search.findNode(item);
            if (result != null && result.getStoreType() == "archive") {
                result = null;
            }
         }
         else if (itemValueType == "xpath")
         {
            result = search.xpathSearch(itemValueTypeHint.replace("%VALUE%", search.ISO9075Encode(item)))[0];
         }
         
         if (result != null)
         {
            // create a separate object if the node represents a user or group
            if (result.isSubType("cm:person"))
            {
               result = createPersonResult(result);
            }
            else if (result.isSubType("cm:authorityContainer"))
            {
               result = createGroupResult(result);
            }

            results.push(
            {
               item: result,
               visibleName: substitude.formatNodeTitle(substituteParent != "none" ? substituteParent : result.nodeRef.toString(), ("" + itemNameSubstituteString)),
               selectedVisibleName: substitude.formatNodeTitle(substituteParent != "none" ? substituteParent : result.nodeRef.toString(), ("" + selectedItemsNameSubstituteString))
            });
         }
      }
   }

   if (logger.isLoggingEnabled())
       logger.log("#items = " + count + ", #results = " + results.length);

   model.results = results;
   model.additionalProperties = additionalProperties;
}

main();
