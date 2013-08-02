<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">
<import resource="classpath:/alfresco/site-webscripts/ru/it/lecm/documents/utils/permission-utils.js">

function main()
{
   // fetch the request params required by the search component template
   /*var siteId = (page.url.templateArgs["site"] != null) ? page.url.templateArgs["site"] : "";
   var siteTitle = null;
   if (siteId.length != 0)
   {
      // Call the repository for the site profile
      var json = remote.call("/api/sites/" + siteId);
      if (json.status == 200)
      {
         // Create javascript objects from the repo response
         var obj = eval('(' + json + ')');
         if (obj)
         {
            siteTitle = (obj.title.length != 0) ? obj.title : obj.shortName;
         }
      }
   }

   // get the search sorting fields from the config
   var sortables = config.scoped["Search"]["sorting"].childrenMap["sort"];
   var sortFields = [];
   for (var i = 0, sort, label; i < sortables.size(); i++)
   {
      sort = sortables.get(i);

      // resolve label text
      label = sort.attributes["label"];
      if (label == null)
      {
         label = sort.attributes["labelId"];
         if (label != null)
         {
            label = msg.get(label);
         }
      }

      // create the model object to represent the sort field definition
      sortFields.push(
      {
         type: sort.value,
         label: label ? label : sort.value
      });
   }

   // Prepare the model
   var repoconfig = config.scoped['Search']['search'].getChildValue('repository-search');
   model.siteId = siteId;
   model.siteTitle = (siteTitle != null ? siteTitle : "");
   model.sortFields = sortFields;
   model.searchTerm = (page.url.args["t"] != null) ? page.url.args["t"] : "";
   model.searchTag = (page.url.args["tag"] != null) ? page.url.args["tag"] : "";
   model.searchSort = (page.url.args["s"] != null) ? page.url.args["s"] : "";
   // config override can force repository search on/off
   model.searchRepo = ((page.url.args["r"] == "true") || repoconfig == "always") && repoconfig != "none";
   model.searchAllSites = (page.url.args["a"] == "true" || siteId.length == 0);

   // Advanced search forms based json query
   model.searchQuery = (page.url.args["q"] != null) ? page.url.args["q"] : ""; */
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
   AlfrescoUtil.param("nodeRef");
   //AlfrescoUtil.param("type");
   model.semanticNodeRef = (model.nodeRef != null) ? model.nodeRef : "";
   model.semanticDocType = "lecm"; //(model.type != null) ? model.type : "";
}

main();

/*


function main() {
    AlfrescoUtil.param("nodeRef");
	model.documentsList = getDocuments(model.nodeRef);
}

function getDocuments(nodeRef) {
    var url = '/lecm/br5/semantic/documents/documents-by-document?nodeRef=' + nodeRef;
    var result = remote.connect("alfresco").get(url);
    if (result.status == 200) {
		return eval('(' + result + ')');
    }
	return null;
}

main(); */