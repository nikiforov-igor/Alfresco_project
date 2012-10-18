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
      itemOpenSubstituteSymbol = "{",
      itemCloseSubstituteSymbol = "}",
      numItems = jsonItems.length(),
      item, result;
   
   if (json.has("itemValueType"))
   {
      var jsonValueTypes = json.get("itemValueType").split(";");
      itemValueType = jsonValueTypes[0];
      itemValueTypeHint = (jsonValueTypes.length > 1) ? jsonValueTypes[1] : "";
   }
   if (json.has("itemNameSubstituteString"))
   {
       itemNameSubstituteString = json.get("itemNameSubstituteString");
   }
   if (json.has("itemOpenSubstituteSymbol"))
   {
       itemOpenSubstituteSymbol = json.get("itemOpenSubstituteSymbol");
   }
   if (json.has("itemCloseSubstituteSymbol"))
   {
       itemCloseSubstituteSymbol = json.get("itemCloseSubstituteSymbol");
   }

   var nameParams = splitString(itemNameSubstituteString, itemOpenSubstituteSymbol, itemCloseSubstituteSymbol);
   
   for (count = 0; count < numItems; count++)
   {
      item = jsonItems.get(count);
      if (item != "")
      {
         result = null;
         if (itemValueType == "nodeRef")
         {
            result = search.findNode(item);
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

            var visibleName = itemNameSubstituteString;
            for each(var field in nameParams) {
                visibleName = visibleName.replace(itemOpenSubstituteSymbol + field + itemCloseSubstituteSymbol, result.properties[field]);
            }

            results.push(
            {
               item: result,
               visibleName: visibleName
            });
         }
      }
   }

   if (logger.isLoggingEnabled())
       logger.log("#items = " + count + ", #results = " + results.length);

   model.results = results;
}

function splitString(string, openSymbol, closeSymbol) {
    var result = [];
    if (string.indexOf(openSymbol) != -1 && string.indexOf(closeSymbol) != -1) {
        var openIndex = string.indexOf(openSymbol);
        var closeIndex = string.indexOf(closeSymbol);
        result.push(string.substring(openIndex + 1, closeIndex));
        var lastOpenIndex = string.lastIndexOf(openSymbol);
        var lastCloseIndex = string.lastIndexOf(closeSymbol);
        while (openIndex != lastOpenIndex && closeIndex != lastCloseIndex) {
            var openIndex = string.indexOf(openSymbol, openIndex + 1);
            var closeIndex = string.indexOf(closeSymbol, closeIndex + 1);
            result.push(string.substring(openIndex + 1, closeIndex));
        }
    }
    return result;
}

main();
