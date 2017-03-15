<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary-v2/evaluator.lib.js">
    <import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary-v2/parse-args.lib.js">

    /**
     * Main entry point: Return single document or folder given it's nodeRef
     *
     * @method getDoclist
     */
        function getDoclist() {
        // Use helper function to get the arguments
        var parsedArgs = ParseArgs.getParsedArgs();
        if (parsedArgs === null) {
            return;
        }

        parsedArgs.pathNode = ParseArgs.resolveNode(parsedArgs.nodeRef);
        if (!parsedArgs.pathNode ) {
            status.code = 404;
            status.message = "NodeRef " + parsedArgs.nodeRef + " not found. It maybe deleted or never existed";
            status.redirect = true;
            return null;
        }

        parsedArgs.location = Common.getLocation(parsedArgs.pathNode, parsedArgs.libraryRoot);

        var favourites = Common.getFavourites(),
            node = parsedArgs.pathNode;

        var isThumbnailNameRegistered = thumbnailService.isThumbnailNameRegistered(THUMBNAIL_NAME),
            thumbnail = null,
            item = Evaluator.run(node);

        item.isFavourite = (favourites[node.nodeRef] === true);
        item.likes = Common.getLikes(node);
        item.location =
        {
            site: parsedArgs.location.site,
            siteTitle: parsedArgs.location.siteTitle,
            container: parsedArgs.location.container,
            containerType: parsedArgs.location.containerType,
            path: parsedArgs.location.path,
            file: node.name
        };

        item.parent = {};
        if (node.parent != null) {
            item.parent = Evaluator.run(node.parent, true);
        }

        // Special case for container and libraryRoot nodes
        if ((parsedArgs.location.containerNode && String(parsedArgs.location.containerNode.nodeRef) == String(node.nodeRef)) ||
            (parsedArgs.libraryRoot && String(parsedArgs.libraryRoot.nodeRef) == String(node.nodeRef))) {
            item.location.file = "";
        }

        // Is our thumbnail type registered?
        if (isThumbnailNameRegistered && item.node.isSubType("cm:content")) {
            // Make sure we have a thumbnail.
            thumbnail = item.node.getThumbnail(THUMBNAIL_NAME);
            if (thumbnail === null && item.node.properties["cm:content"].getReader() != null) {
                // No thumbnail, so queue creation
                item.node.createThumbnail(THUMBNAIL_NAME, true);
            }
        }

        item.typeTitle = base.getNodeTypeLabel(node.nodeRef);

        return (
        {
            container: parsedArgs.rootNode,
            onlineEditing: utils.moduleInstalled("org.alfresco.module.vti"),
            item: item,
            customJSON: slingshotDocLib.getJSON()
        });
    }

/**
 * Document List Component: doclist
 */
model.doclist = getDoclist();
model.isMlSupported = lecmMessages.isMlSupported();
