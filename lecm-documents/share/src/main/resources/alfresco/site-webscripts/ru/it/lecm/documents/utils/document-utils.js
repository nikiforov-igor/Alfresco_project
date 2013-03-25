<import resource="classpath:/alfresco/site-webscripts/org/alfresco/components/documentlibrary/data/surf-doclist.lib.js">

var this_DocumentUtils = this;

var DocumentUtils =
{
   getRootNode: function getRootNode()
   {
      var rootNode = "alfresco://company/home",
         repoConfig = config.scoped["RepositoryLibrary"]["root-node"];

      if (repoConfig !== null)
      {
         rootNode = repoConfig.value;
      }
      return rootNode;
   },

   getNodeDetails: function getNodeDetails(nodeRef, site, options)
   {
      if (nodeRef)
      {
         var url = '/lecm/document/node/' + nodeRef.replace('://', '/');
         if (!site)
         {
            // Repository mode
            url += "?libraryRoot=" + encodeURIComponent(DocumentUtils.getRootNode());
         }
         var result = remote.connect("alfresco").get(url);

         if (result.status == 200)
         {
            var details = eval('(' + result + ')');
            if (details && (details.item || details.items))
            {
               DocList.processResult(details, options);
               return details;
            }
         }
      }
      return null;
   }
};
