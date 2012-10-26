var Common =
{
    /**
     * Cache for person objects
     */
    PeopleCache: {},

    /**
     * Gets / caches a person object
     *
     * @method getPerson
     * @param username {string} User name
     */
    getPerson: function Common_getPerson(username)
    {
        if (username == null || username == "")
        {
            return null;
        }

        if (typeof Common.PeopleCache[username] != "object")
        {
            var person = people.getPerson(username);
            if (person == null)
            {
                if (username == "System" || username.match("^System@") == "System@")
                {
                    // special case for the System users
                    person =
                    {
                        properties:
                        {
                            userName: "System",
                            firstName: "System",
                            lastName: "User"
                        },
                        assocs: {}
                    };
                }
                else
                {
                    // missing person - may have been deleted from the database
                    person =
                    {
                        properties:
                        {
                            userName: username,
                            firstName: "",
                            lastName: ""
                        },
                        assocs: {}
                    };
                }
            }
            Common.PeopleCache[username] =
            {
                userName: person.properties.userName,
                firstName: person.properties.firstName,
                lastName: person.properties.lastName,
                displayName: (person.properties.firstName + " " + person.properties.lastName).replace(/^\s+|\s+$/g, "")
            };
            if (person.assocs["cm:avatar"] != null)
            {
                Common.PeopleCache[username].avatar = person.assocs["cm:avatar"][0];
            }
        }
        return Common.PeopleCache[username];
    }
};

var ParseArgs =
{
    /**
     * Get and parse arguments
     *
     * @method getParsedArgs
     * @param containerType {string} Optional: Node Type of container to create if it doesn't exist, defaults to "cm:folder"
     * @return {array|null} Array containing the validated input parameters
     */
    getParsedArgs: function ParseArgs_getParsedArgs(containerType)
    {
        var rootNode = null,
            nodeRef = null,
            listNode = null;

        if (url.templateArgs.store_type !== null)
        {
            /**
             * nodeRef input: store_type, store_id and id
             */
            var storeType = url.templateArgs.store_type,
                storeId = url.templateArgs.store_id,
                id = url.templateArgs.id;

            nodeRef = storeType + "://" + storeId + "/" + id;
            rootNode = ParseArgs.resolveNode(nodeRef);
            if (rootNode == null)
            {
                rootNode = search.findNode(nodeRef);
                if (rootNode === null)
                {
                    status.setCode(status.STATUS_NOT_FOUND, "Not a valid nodeRef: '" + nodeRef + "'");
                    return null;
                }
            }

            listNode = rootNode;
        }
        else
        {
            /**
             * Site and container input
             */
            var siteId = url.templateArgs.site,
                containerId = url.templateArgs.container,
                listId = url.templateArgs.list,
                siteNode = siteService.getSite(siteId);

            if (siteNode === null)
            {
                status.setCode(status.STATUS_NOT_FOUND, "Site not found: '" + siteId + "'");
                return null;
            }

            rootNode = siteNode.getContainer(containerId);
            if (rootNode === null)
            {
                rootNode = siteNode.createAndSaveContainer(containerId, containerType || "cm:folder", "Data Lists");
                if (rootNode === null)
                {
                    status.setCode(status.STATUS_NOT_FOUND, "Data Lists container '" + containerId + "' not found in '" + siteId + "'. (No permission?)");
                    return null;
                }
            }
            listNode = rootNode;

            if (listId !== null)
            {
                listNode = rootNode.childByNamePath(listId);
                if (listNode === null)
                {
                    status.setCode(status.STATUS_NOT_FOUND, "List not found: '" + listId + "'");
                    return null;
                }
            }
        }

        // Filter
        var filter = null;
        if (args.filter)
        {
            filter =
            {
                filterId: args.filter,
                filterData: args.filterData
            }
        }
        else if (typeof json !== "undefined" && json.has("filter"))
        {
            var filterJSON = json.get("filter");
            if (filterJSON != null)
            {
                filter =
                {
                    filterId: filterJSON.get("filterId"),
                    filterData: filterJSON.get("filterData")
                }
            }
            else
            {
                filter =
                {
                    filterId: "all"
                }
            }
        }

        var objRet =
        {
            rootNode: rootNode,
            listNode: listNode,
            nodeRef: String(listNode.nodeRef),
            filter: filter
        };

        return objRet;
    },

    /**
     * Resolve "virtual" nodeRefs into nodes
     *
     * @method resolveVirtualNodeRef
     * @deprecated for ParseArgs.resolveNode
     */
    resolveVirtualNodeRef: function ParseArgs_resolveVirtualNodeRef(nodeRef)
    {
        if (logger.isLoggingEnabled())
        {
            logger.log("WARNING: ParseArgs.resolveVirtualNodeRef is deprecated for ParseArgs.resolveNode");
        }
        return ParseArgs.resolveNode(nodeRef);
    },

    /**
     * Resolve "virtual" nodeRefs, nodeRefs and xpath expressions into nodes
     *
     * @method resolveNode
     * @param reference {string} "virtual" nodeRef, nodeRef or xpath expressions
     * @return {ScriptNode|null} Node corresponding to supplied expression. Returns null if node cannot be resolved.
     */
    resolveNode: function ParseArgs_resolveNode(reference)
    {
        var node = null;
        try
        {
            if (reference == "alfresco://company/home")
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
        }
        catch (e)
        {
            return null;
        }
        return node;
    }
};


function main()
{
    var nodeRef = null,
        rootNode = null,
        params = {};

    if (url.templateArgs.store_type !== null)
    {
        /**
         * nodeRef input: store_type, store_id and id
         */
        var storeType = url.templateArgs.store_type,
            storeId = url.templateArgs.store_id,
            id = url.templateArgs.id;

        nodeRef = storeType + "://" + storeId + "/" + id;
        rootNode = ParseArgs.resolveNode(nodeRef);
        if (rootNode == null)
        {
            rootNode = search.findNode(nodeRef);
            if (rootNode === null)
            {
                status.setCode(status.STATUS_NOT_FOUND, "Not a valid nodeRef: '" + nodeRef + "'");
                return null;
            }
        }

        params.nodeRef = nodeRef;
        params.rootNode = rootNode;
    }

    // Multiple input files in the JSON body?
    var items = getMultipleInputValues("nodeRefs");
    if (typeof items != "string")
    {
        params.items = items;
    }

    // Check runAction function is provided the action's webscript
    if (typeof runAction != "function")
    {
        status.setCode(status.STATUS_BAD_REQUEST, "Action webscript must provide runAction() function.");
        return;
    }

    // Actually run the action
    var results = runAction(params);
    if (results)
    {
        if (typeof results == "string")
        {
            status.setCode(status.STATUS_INTERNAL_SERVER_ERROR, results);
        }
        else if (typeof results.status == "object")
        {
            // Status fields have been manually set
            status.redirect = true;
            for (var s in results.status)
            {
                status[s] = results.status[s];
            }
        }
        else
        {
            /**
             * NOTE: Webscripts run within one transaction only.
             * If a single operation fails, the transaction is marked for rollback and all
             * previous (successful) operations are also therefore rolled back.
             * We therefore need to scan the results for a failed operation and mark the entire
             * set of operations as failed.
             */
            var overallSuccess = true,
                successCount = 0,
                failureCount = 0;

            for (var i = 0, j = results.length; i < j; i++)
            {
                overallSuccess = overallSuccess && results[i].success;
                results[i].success ? ++successCount : ++failureCount;
            }
            model.overallSuccess = overallSuccess;
            model.successCount = successCount;
            model.failureCount = failureCount;
            model.results = results;
        }
    }
}

/**
 * Get multiple input values
 *
 * @method getMultipleInputValues
 * @return {array|string} Array containing multiple values, or string error
 */
function getMultipleInputValues(param)
{
    var values = [],
        error = null;

    try
    {
        // Was a JSON parameter list supplied?
        if (typeof json != "undefined")
        {
            if (!json.isNull(param))
            {
                var jsonValues = json.get(param);
                // Convert from JSONArray to JavaScript array
                for (var i = 0, j = jsonValues.length(); i < j; i++)
                {
                    values.push(jsonValues.get(i));
                }
            }
        }
    }
    catch(e)
    {
        error = e.toString();
    }

    // Return the values array, or the error string if it was set
    return (error !== null ? error : values);
}

/**
 * Delete multiple items action
 * @method DELETE
 */

/**
 TODO код выше скопирован из action.lib.js и parse-args.lib.js
 Удалить после того, как будет отлажен
 */
/**
 * Entrypoint required by action.lib.js
 *
 * @method runAction
 * @param p_params {object} Object literal containing items array
 * @return {object|null} object representation of action results
 */
function runAction(p_params) {
    var results = [],
        items = p_params.items,
        item, result, nodeRef;

    // Must have array of items
    if (!items || items.length == 0) {
        status.setCode(status.STATUS_BAD_REQUEST, "No items supplied in JSON body.");
        return;
    }
    var full = args["full"];
    if (full == null) {
        full = false;
    }
    var type = args["deletedType"];

    for (item in items) {
        nodeRef = items[item];
        result =
        {
            nodeRef:nodeRef,
            action:"deleteItem",
            success:false
        };

        try {
            var itemNode = search.findNode(nodeRef);
            if (itemNode != null) {
                var sAssocs;
                if (type != null) { // delete assocs by type, element dont modified
                    sAssocs = itemNode.sourceAssocs[type];
                    if (full) { // remove object what has assocs with given type
                        var res = true;
                        for (index in sAssocs) {
                            var assocEl = sAssocs[index];
                            //assocEl.removeAssociation(itemNode, type);
                            //assocEl.save();
                            res = assocEl.remove();
                        }
                        result.success = res;
                    } else { // remove only links
                        for (index in sAssocs) {
                            var assocEl = sAssocs[key];
                            assocEl.removeAssociation(itemNode, type);
                        }
                        result.success = true;
                    }
                } else { // delete all assocs, mark object as inactive
                    sAssocs = itemNode.getSourceAssocs();
                    if (full) {
                        for (key in sAssocs) {
                            var assocsList = sAssocs[key];
                            for (index in assocsList) {
                                var target = assocsList[index];
                                //target.removeAssociation(itemNode, key);
                                target.remove();
                            }
                        }
                        // mark as deleted
                        itemNode.properties["lecm-dic:active"] = false;
                        result.success = itemNode.save();
                    } else {
                        for (key in sAssocs) {
                            var assocsList = sAssocs[key];
                            for (index in assocsList) {
                                var target = assocsList[index];
                                target.removeAssociation(itemNode, key);
                            }
                        }
                        // mark as deleted
                        itemNode.properties["lecm-dic:active"] = false;
                        result.success = itemNode.save();
                    }
                }
            }
        }
        catch (e) {
            result.success = false;
        }

        results.push(result);
    }

    return results;
}

/* Bootstrap action script */
main();
