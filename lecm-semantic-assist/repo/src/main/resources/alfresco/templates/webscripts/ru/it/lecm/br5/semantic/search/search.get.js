<import resource="classpath:/alfresco/templates/webscripts/ru/it/lecm/br5/semantic/search/search.lib.js">
function main()
{
   semanticQuery = getSemanticQuery();
   var params =
   {
      siteId: (args.site !== null) ? args.site : null,
      containerId: (args.container !== null) ? args.container : null,
      repo: (args.repo !== null) ? (args.repo == "true") : false,
      term: (args.term !== null) ? args.term : null,
      tag: (args.tag !== null) ? args.tag : null,
      query: (args.query !== null) ? args.query : null,
      sort: (args.sort !== null) ? args.sort : null,
      maxResults: (args.maxResults !== null) ? parseInt(args.maxResults, 10) : DEFAULT_MAX_RESULTS,
	  semanticQuery: semanticQuery
   };

   model.data = getSearchResults(params);
}

function getSemanticQuery(){
   semanticDocType = (args.semanticDocType != null) ? args.semanticDocType : null;
   semanticTag = (args.semanticTag != null) ? args.semanticTag : null;
   semanticNodeRef = (args.semanticNodeRef != null) ? args.semanticNodeRef : null;
   if (semanticDocType){
	   if ( semanticTag ){
		  return integration.getQueryByTag(semanticTag,semanticDocType);
	   }
	   if ( semanticNodeRef ){
		  return integration.getQueryByDocument(semanticNodeRef,semanticDocType);
	   }
   }
   return "something";
}

main();