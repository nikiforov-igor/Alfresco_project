/**
 * Node Metadata Retrieval Service GET method
 */
function main()
{
   var json = "{}";
   
   // allow for content to be loaded from id
   if (args["nodeRef"] != null)
   {
   	var nodeRef = args["nodeRef"];
   	node = search.findNode(nodeRef);
   	
   	if (node != null)
   	{
		json = appUtils.toJSON(node, args["shortQNames"] != null);
   	}
   }
   
   // store node onto model
   model.json = json;
}

main();