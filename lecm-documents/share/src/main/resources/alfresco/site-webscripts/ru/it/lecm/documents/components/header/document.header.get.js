<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   AlfrescoUtil.param("nodeRef");
   var nodeDetails = AlfrescoUtil.getNodeDetails(model.nodeRef);
   if (nodeDetails)
   {
      model.item = nodeDetails.item;
      model.documentName = nodeDetails.item.node.properties["cm:name"];
   }
}

main();