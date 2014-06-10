/**
 * Search component GET method
 */

function main()
{
    // Prepare the model
   model.siteId = "";
   model.siteTitle =  "";
   model.sortFields = "";
   model.searchTerm = "";
   model.searchTag =  "";
   model.searchSort = "";
   // config override can force repository search on/off
   model.searchRepo = true;
   model.searchAllSites = true;

   // Advanced search forms based json query
   model.searchQuery = "";
   model.semanticTag = (page.url.args["tag"] != null) ? page.url.args["tag"] : "";
   model.semanticNodeRef = (page.url.args["nodeRef"] != null) ? page.url.args["nodeRef"] : "";
   model.semanticDocType = (page.url.args["type"] != null) ? page.url.args["type"] : "";
}

main();
