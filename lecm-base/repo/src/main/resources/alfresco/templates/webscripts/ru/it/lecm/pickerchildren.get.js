function main()
{
   var argsFilterType = args['filterType'],
      argsSelectableType = args['selectableType'],
      argsSearchTerm = args['searchTerm'],
      argsMaxResults = args['size'],
      argsXPath = args['xpath'],
      argsRootNode = args['rootNode'],
      argsNameSubstituteString = args['nameSubstituteString'],
      argsOpenSubstituteSymbol = args['openSubstituteSymbol'],
      argsCloseSubstituteSymbol = args['closeSubstituteSymbol'],
      pathElements = url.service.split("/"),
      parent = null,
      rootNode = companyhome,
      results = [],
      categoryResults = null,
      resultObj = null,
      lastPathElement = null,
      argsXPathLocation = args['xPathLocation'],
      argsXPathRoot = args['xPathRoot'];

   var nameParams = splitString(argsNameSubstituteString, argsOpenSubstituteSymbol, argsCloseSubstituteSymbol);
   
   if (logger.isLoggingEnabled())
   {
      logger.log("children type = " + url.templateArgs.type);
      logger.log("argsSelectableType = " + argsSelectableType);
      logger.log("argsFilterType = " + argsFilterType);
      logger.log("argsSearchTerm = " + argsSearchTerm);
      logger.log("argsMaxResults = " + argsMaxResults);
      logger.log("argsXPath = " + argsXPath);
      logger.log("nameSubstituteString = " + argsNameSubstituteString);
      logger.log("openSubstituteSymbol = " + argsOpenSubstituteSymbol);
      logger.log("closeSubstituteSymbol = " + argsCloseSubstituteSymbol);
      logger.log("nameParams = " + nameParams);
      logger.log("argsXPathLocation = " + argsXPathLocation);
      logger.log("argsXPathRoot = " + argsXPathRoot);
    }
         
   try
   {
      // construct the NodeRef from the URL
      var nodeRef = url.templateArgs.store_type + "://" + url.templateArgs.store_id + "/" + url.templateArgs.id;
      
      // determine if we need to resolve the parent NodeRef
      
      if (argsXPath != null)
      {
         // resolve the provided XPath to a NodeRef
         var nodes = search.xpathSearch(argsXPath);
         if (nodes.length > 0)
         {
            nodeRef = String(nodes[0].nodeRef);
         }
      }
      if (argsXPathLocation != null)
      {
          var root = companyhome;
          // resolve the root for XPath
          if (argsXPathRoot != null) {
              var node = resolveNode(argsXPathRoot);
              if (node != null) {
                  root = node;
              }
          }
          var nodes = root.childrenByXPath(argsXPathLocation);
          if (nodes.length > 0)
          {
              nodeRef = String(nodes[0].nodeRef);
          }
      }

      // default to max of 100 results
      var maxResults = 100;
      if (argsMaxResults != null)
      {
         // force the argsMaxResults var to be treated as a number
         maxResults = parseInt(argsMaxResults, 10) || maxResults;
      }

      // if the last path element is 'doclib' or 'siblings' find parent node
      if (pathElements.length > 0)
      {
         lastPathElement = pathElements[pathElements.length-1];
         
         if (logger.isLoggingEnabled())
            logger.log("lastPathElement = " + lastPathElement);
         
         if (lastPathElement == "siblings")
         {
            // the provided nodeRef is the node we want the siblings of so get it's parent
            var node = search.findNode(nodeRef);
            if (node !== null)
            {
               nodeRef = node.parent.nodeRef;
            }
            else
            {
               // if the provided node was not found default to companyhome
               nodeRef = "alfresco://company/home";
            }
         }
         else if (lastPathElement == "doclib")
         {
            // we want to find the document library for the nodeRef provided
            nodeRef = findDoclib(nodeRef);
         }
      }

      if (url.templateArgs.type == "node")
      {
        var childNodes = [];

        parent = resolveNode(nodeRef);
        if (parent === null) {
            status.setCode(status.STATUS_NOT_FOUND, "Not a valid nodeRef: '" + nodeRef + "'");
            return null;
        }
        if (argsRootNode != null){
            rootNode = resolveNode(argsRootNode) || companyhome;
        }

        if (argsSearchTerm == null || argsSearchTerm == "")  {
            var ignoreTypes = null;
            if (argsFilterType != null)
            {
                 if (logger.isLoggingEnabled()) {
                    logger.log("ignoring types = " + argsFilterType);
                 }
                 ignoreTypes = argsFilterType.split(',');
            }

            childNodes = parent.childFileFolders(true, true, ignoreTypes, -1, maxResults, 0, "cm:name", true, null).getPage();
        } else {
            var filterParams = getFilterParams(argsSearchTerm, parent);
            query = filterParams.query;

            // Query the nodes - passing in default sort and result limit parameters
            if (query !== "")
            {
                childNodes = search.query(
                {
                    query: query,
                    language: filterParams.language,
                    page:
                        {
                        maxItems: (filterParams.limitResults ? parseInt(filterParams.limitResults, 10) : 0)
                        },
                     sort: filterParams.sort,
                     templates: filterParams.templates,
                     namespace: (filterParams.namespace ? filterParams.namespace : null)
                 });
            }
         }

         // retrieve the children of this node

         // Ensure folders and folderlinks appear at the top of the list
         var containerResults = new Array(),
            contentResults = new Array();

         for each (var result in childNodes)
         {
	        if (!result.hasAspect("lecm-dic:aspect_active") || result.properties["lecm-dic:active"]) {
	            if (result.isContainer || result.type == "{http://www.alfresco.org/model/application/1.0}folderlink")
	            {
	               // wrap result and determine if it is selectable in the UI
	               resultObj =
	               {
	                  item: result
	               };
	               resultObj.selectable = isItemSelectable(result, argsSelectableType);

	               containerResults.push(resultObj);
	            }
	            else
	            {
	               // wrap result and determine if it is selectable in the UI
	               resultObj =
	               {
	                  item: result
	               };
	               resultObj.selectable = isItemSelectable(result, argsSelectableType);

	               contentResults.push(resultObj);
	            }

	            var visibleName = argsNameSubstituteString;
	            for each(var field in nameParams) {
	                visibleName = visibleName.replace(argsOpenSubstituteSymbol + field + argsCloseSubstituteSymbol, result.properties[field]);
	            }
	            resultObj.visibleName = visibleName;
            }
         }

         results = containerResults.concat(contentResults);
      }
      else if (url.templateArgs.type == "category")
      {
         var catAspect = (args["aspect"] != null) ? args["aspect"] : "cm:generalclassifiable";

         // TODO: Better way of finding this
         var rootCategories = classification.getRootCategories(catAspect);
         if (rootCategories != null && rootCategories.length > 0)
         {
            rootNode = rootCategories[0].parent;
            if (nodeRef == "alfresco://category/root")
            {
               parent = rootNode;
               categoryResults = classification.getRootCategories(catAspect);
            }
            else
            {
               parent = search.findNode(nodeRef);
               categoryResults = parent.children;
            }
            
            if (argsSearchTerm != null)
            {
               var filteredResults = [];
               for each (result in categoryResults)
               {
                  if (result.properties.name.indexOf(argsSearchTerm) == 0)
                  {
                     filteredResults.push(result);
                  }
               }
               categoryResults = filteredResults.slice(0);
            }
            categoryResults.sort(sortByName);
            
            // make each result an object and indicate it is selectable in the UI
            for each (var result in categoryResults)
            {
               results.push(
               { 
                  item: result, 
                  selectable: true 
               });
            }
         }
      }
      else if (url.templateArgs.type == "authority")
      {
         if (argsSelectableType == "cm:person")
         {
            findUsers(argsSearchTerm, maxResults, results);
         }
         else if (argsSelectableType == "cm:authorityContainer")
         {
            findGroups(argsSearchTerm, maxResults, results);
         }
         else
         {
            // combine groups and users
            findGroups(argsSearchTerm, maxResults, results);
            findUsers(argsSearchTerm, maxResults, results);
         }
      }
      
      if (logger.isLoggingEnabled())
         logger.log("Found " + results.length + " results");
   }
   catch (e)
   {
      var msg = e.message;
      
      if (logger.isLoggingEnabled())
         logger.log(msg);
      
      status.setCode(500, msg);
      
      return;
   }

   model.parent = parent;
   model.rootNode = rootNode;
   model.results = results;
}

function isItemSelectable(node, selectableType)
{
   var selectable = true;
   
   if (selectableType !== null && selectableType !== "")
   {
      selectable = node.isSubType(selectableType);
      
      if (!selectable)
      {
         // the selectableType could also be an aspect,
         // if the node has that aspect it is selectable
         selectable = node.hasAspect(selectableType);
      }
   }
   
   return selectable;
}

/* Sort the results by case-insensitive name */
function sortByName(a, b)
{
   return (b.properties.name.toLowerCase() > a.properties.name.toLowerCase() ? -1 : 1);
}

function findUsers(searchTerm, maxResults, results)
{
   var paging = utils.createPaging(maxResults, -1);
   var searchResults = groups.searchUsers(searchTerm, paging, "lastName");
   
   // create person object for each result
   for each(var user in searchResults)
   {
      if (logger.isLoggingEnabled())
         logger.log("found user = " + user.userName);
      
      // add to results
      results.push(
      {
         item: createPersonResult(user.person),
         selectable: true 
      });
   }
}

function findGroups(searchTerm, maxResults, results)
{
   if (logger.isLoggingEnabled())
      logger.log("Finding groups matching pattern: " + searchTerm);
   
   var paging = utils.createPaging(maxResults, 0);
   var searchResults = groups.getGroupsInZone(searchTerm, "APP.DEFAULT", paging, "displayName");
   for each(var group in searchResults)
   {
      if (logger.isLoggingEnabled())
         logger.log("found group = " + group.fullName);
         
      // add to results
      results.push(
      {
         item: createGroupResult(group.groupNode),
         selectable: true 
      });
   }
   
   // sort the groups by name alphabetically
   if (results.length > 0)
   {
      results.sort(function(a, b)
      {
         return (a.item.properties.name < b.item.properties.name) ? -1 : (a.item.properties.name > b.item.properties.name) ? 1 : 0;
      });
   }
}

/**
 * Returns the nodeRef of the document library of the site the
 * given nodeRef is located within. If the nodeRef provided does
 * not live within a site "alfresco://company/home" is returned.
 * 
 * @param nodeRef The node to find the document library for
 * @return The nodeRef of the doclib or "alfresco://company/home" if the node
 *         is not located within a site
 */
function findDoclib(nodeRef)
{
   var resultNodeRef = "alfresco://company/home";
   
   // find the given node
   var node = search.findNode(nodeRef);
   if (node !== null)
   {
      // get the name of the site
      var siteName = node.siteShortName;
      
      if (logger.isLoggingEnabled())
         logger.log("siteName = " + siteName);
      
      // if the node is in a site find the document library node using an XPath search
      if (siteName !== null)
      {
         var nodes = search.xpathSearch("/app:company_home/st:sites/cm:" + search.ISO9075Encode(siteName) + "/cm:documentLibrary");
         if (nodes.length > 0)
         {
            // there should only be 1 result, get the first one
            resultNodeRef = String(nodes[0].nodeRef);
         }
      }
   }
   
   return resultNodeRef;
}

/**
 * Resolve "virtual" nodeRefs, nodeRefs and xpath expressions into nodes
 *
 * @method resolveNode
 * @param reference {string} "virtual" nodeRef, nodeRef or xpath expressions
 * @return {ScriptNode|null} Node corresponding to supplied expression. Returns null if node cannot be resolved.
 */
function resolveNode(reference)
{
   var node = null;
   try
   {
      if (reference == "alfresco://company/home" || reference == "{companyhome}")
      {
         node = companyhome;
      }
      else if (reference == "alfresco://user/home")
      {
         node = userhome;
      }
      else if (reference == "alfresco://sites/home")
      {
         node = companyhome.childrenByXPath("st:sites")[0];
      }
      else if (reference.indexOf("://") > 0)
      {
         node = search.findNode(reference);
      }
      else if (reference.substring(0, 1) == "/")
      {
         node = search.xpathSearch(reference)[0];
      }
       else if (reference == "{organization}") {
        node = companyhome.childByNamePath("Организация");
      }
   }
   catch (e)
   {
      return null;
   }
   return node;
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

/**
 * Creates an Object representing the given person node.
 *
 * @method createPersonResult
 * @param node
 * @return Object representing the person
 */
function createPersonResult(node)
{
    var personObject =
    {
        typeShort: node.typeShort,
        isContainer: false,
        properties: {},
        displayPath: node.displayPath,
        nodeRef: "" + node.nodeRef
    }

    // define properties for person
    personObject.properties.userName = node.properties.userName;
    personObject.properties.name = (node.properties.firstName ? node.properties.firstName + " " : "") +
        (node.properties.lastName ? node.properties.lastName : "") +
        " (" + node.properties.userName + ")";
    personObject.properties.jobtitle = (node.properties.jobtitle ? node.properties.jobtitle  : "");

    return personObject;
}

/**
 * Creates an Object representing the given group node.
 *
 * @method createGroupResult
 * @param node
 * @return Object representing the group
 */
function createGroupResult(node)
{
    var groupObject =
    {
        typeShort: node.typeShort,
        isContainer: false,
        properties: {},
        displayPath: node.displayPath,
        nodeRef: "" + node.nodeRef
    }

    // find most appropriate name for the group
    var name = node.properties.name;
    if (node.properties.authorityDisplayName != null && node.properties.authorityDisplayName.length > 0)
    {
        name = node.properties.authorityDisplayName;
    }
    else if (node.properties.authorityName != null && node.properties.authorityName.length > 0)
    {
        var authName = node.properties.authorityName;
        if (authName.indexOf("GROUP_") == 0)
        {
            name = authName.substring(6);
        }
        else
        {
            name = authName;
        }
    }

    // set the name
    groupObject.properties.name = name;

    return groupObject;
}

function getFilterParams(filterData, parentNode)
{
    var xpath = parentNode.getQnamePath();
    var filterParams =
    {
        query: " +PATH:\""+xpath + "//*\"",
        limitResults: null,
        sort: [
            {
                column: "@cm:name",
                ascending: true
            }],
        language: "lucene",
        templates: null
    };
    var columns = filterData.split('#');

    // Max returned results specified?
    var argMax = args.max;
    if ((argMax !== null) && !isNaN(argMax))
    {
        filterParams.limitResults = argMax;
    }
    var params = "",
        or = " OR",
        ampersand = " @";
    for (var i=0; i < columns.length; i++) {
        var namespace = columns[i].split(":");
        if (columns[i+1] == undefined ) {
            or = "";
            ampersand = " @";
        }

        params += ampersand + namespace[0]+"\\:" + namespace[1] + ":"+ '"*' +namespace[2] + '*"' + or;
    }
    filterParams.query += " AND " + "(" + params + " )";
    return filterParams;
}

main();