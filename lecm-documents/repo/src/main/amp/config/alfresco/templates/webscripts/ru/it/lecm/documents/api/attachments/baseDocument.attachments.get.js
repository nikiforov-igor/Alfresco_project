<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary-v2/evaluator.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary-v2/filters.lib.js">
<import resource="classpath:/alfresco/templates/webscripts/org/alfresco/slingshot/documentlibrary-v2/parse-args.lib.js">

function getAttachmentslist() {
	// Use helper function to get the arguments
	var parsedArgs = ParseArgs.getParsedArgs();
	if (parsedArgs === null) {
		return;
	}

	var items = [];

	// Try to find a filter query based on the passed-in arguments
	var allNodes = [],
		favourites = Common.getFavourites();

    var baseDocAssocName = url.templateArgs.baseDocAssocName;
    if (baseDocAssocName) {
        var document = parsedArgs.pathNode;
        if (document) {
            var baseDoc = null;
            while (document && document.hasPermission("Read")) {
                var baseDocs = document.assocs[baseDocAssocName];
                if (baseDocs && baseDocs.length) {
                    baseDoc = baseDocs[0];
                    document = baseDocs[0];
                } else {
                    document = null;
                }
            }

            if (baseDoc) {
                var categories = documentAttachments.getCategories(baseDoc.nodeRef.toString());
                if (categories) {
                    categories.forEach(function (category) {
                        var attachments = documentAttachments.getAttachmentsByCategory(category);
                        if (attachments) {
                            allNodes = allNodes.concat(attachments);
                        }
                    });
                }
            }
        }
    }

	var isThumbnailNameRegistered = thumbnailService.isThumbnailNameRegistered(THUMBNAIL_NAME);

	// Loop through and evaluate each node in this result set
    allNodes.forEach(function (node) {
		// Get evaluated properties.
		var item = Evaluator.run(node);
		if (item !== null) {
			item.isFavourite = (favourites[item.node.nodeRef] === true);
			item.likes = Common.getLikes(node);
			item.location = {};
			item.isInnerAttachment = documentAttachments.isInnerAttachment(item.node);

			// Is our thumbnail type registered?
			if (isThumbnailNameRegistered && item.node.isSubType("cm:content")) {
				// Make sure we have a thumbnail.
				var thumbnail = item.node.getThumbnail(THUMBNAIL_NAME);
				if (thumbnail === null) {
					// No thumbnail, so queue creation
					item.node.createThumbnail(THUMBNAIL_NAME, true);
				}
			}

			items.push(item);
		}
	});

	return items;
};

model.items = getAttachmentslist();