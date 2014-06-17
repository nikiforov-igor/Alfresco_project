function main()
{
    var
        argsXPath = args['xpath'],
        titleProperty = args['titleProperty'],
        resultNode = null,
        resultObj = null,
        argsXPathLocation = args['xPathLocation'],
        argsXPathRoot = args['xPathRoot'],
        argsRootNode = args['rootNode'];

    if (logger.isLoggingEnabled())
    {
        logger.log("argsXPath = " + argsXPath);
        logger.log("argsXPathLocation = " + argsXPathLocation);
        logger.log("argsXPathRoot = " + argsXPathRoot);
        logger.log("rootNode = " + argsRootNode);
    }

    try
    {
        // construct the NodeRef from the URL
        var nodeRef = url.templateArgs.store_type + "://" + url.templateArgs.store_id + "/" + url.templateArgs.id;

        // determine if we need to resolve the parent NodeRef

	    if (argsRootNode != null && argsRootNode.length > 0) {
		    nodeRef = argsRootNode;
	    }
        if (argsXPath != null)
        {
            // resolve the provided XPath to a NodeRef
            var nodes = companyhome.childrenByXPath(argsXPath);
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

        resultNode = resolveNode(nodeRef);
        if (resultNode === null) {
            status.setCode(status.STATUS_NOT_FOUND, "Not a valid nodeRef: '" + nodeRef + "'");
            return null;
        }

        resultObj =
        {
            displayPath: resultNode.displayPath != null ? resultNode.displayPath : "",
            title: resultNode.properties[titleProperty],
            type: resultNode.getTypeShort(),
            nodeRef: resultNode.getNodeRef().toString(),
            isLeaf: "" + !searchCounter.hasChildren(resultNode.getNodeRef().toString(), null),
            isContainer: "" + resultNode.isContainer,
	        hasPermAddChildren: lecmPermission.hasPermission(resultNode.nodeRef, "AddChildren")
        };
    }
    catch (e)
    {
        var msg = e.message;

        if (logger.isLoggingEnabled())
            logger.log(msg);

        status.setCode(500, msg);

        return;
    }

    model.resultNode = resultObj;
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
        if (reference == "alfresco://company/home" || reference == "{companyhome}") {
            node = companyhome;
        } else if (reference == "alfresco://user/home") {
            node = userhome;
        } else if (reference == "alfresco://sites/home") {
            node = companyhome.childrenByXPath("st:sites")[0];
        } else if (reference == "alfresco://user/temp") {
	        node = businessPlatform.getUserTemp();
        } else if (reference.indexOf("://") > 0) {
            node = search.findNode(reference);
        } else if (reference.substring(0, 1) == "/") {
            node = search.xpathSearch(reference)[0];
        } else if (reference == "{organization}") {
            node = companyhome.childByNamePath("Организация");
        }
    }
    catch (e)
    {
        return null;
    }
    return node;
}

main();