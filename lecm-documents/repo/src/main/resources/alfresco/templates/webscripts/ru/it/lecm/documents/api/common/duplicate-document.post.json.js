<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/datalists/action/action.lib.js">

function runAction(p_params)
{
   var results = [],
      parentNode = p_params.rootNode,
      items = p_params.items,
      index, result, nodeRef;

   // Must have parent node and array of items
   if (!parentNode)
   {
      status.setCode(status.STATUS_BAD_REQUEST, "No parent node supplied on URL.");
      return;
   }
   if (!items || items.length === 0)
   {
      status.setCode(status.STATUS_BAD_REQUEST, "No items supplied in JSON body.");
      return;
   }
   
   for (index in items)
   {
      nodeRef = items[index];
      result =
      {
         nodeRef: nodeRef,
         action: "duplicateItem",
         success: false
      };

      try
      {
	      var createdNode = documentScript.dublicateDocument(nodeRef);
	      if (createdNode != null) {
		      result.nodeRef = createdNode.nodeRef.toString();
		      result.success = true;
	      }
      }
      catch (e)
      {
         result.success = false;
      }

      results.push(result);
   }

   return results;
}

/* Bootstrap action script */
main();
