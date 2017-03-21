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

	var documentNode = parsedArgs.pathNode;
	var baseDocument = null;
	var baseDocAssoc = null;
    if (documentNode.typeShort == "lecm-resolutions:document") {
        baseDocAssoc = baseDocument.assocs["lecm-resolutions:base-document-assoc"];
    } else if (documentNode.typeShort == "lecm-errands:document") {
        baseDocAssoc = baseDocument.assocs["lecm-errands:base-assoc"];
    }
	if (baseDocAssoc != null && baseDocAssoc.length > 0) {
		baseDocument = baseDocAssoc[0];
	} else {
		baseDocument = null;
	}
	if (baseDocument != null) {
		var categories = documentAttachments.getCategories(baseDocument.nodeRef.toString());
		if (categories != null) {
			for (var i = 0; i < categories.length; i++) {
				var attachments = documentAttachments.getAttachmentsByCategory(categories[i]);
				if (attachments != null && attachments.length > 0) {
					for (var j = 0; j < attachments.length; j++) {
						allNodes.push(attachments[j]);
					}
				}
			}
		}
	}

	var isThumbnailNameRegistered = thumbnailService.isThumbnailNameRegistered(THUMBNAIL_NAME),
		thumbnail = null,
		item;

	// Loop through and evaluate each node in this result set
	for each(node in allNodes) {
		// Get evaluated properties.
		item = Evaluator.run(node);
		if (item !== null) {
			item.isFavourite = (favourites[item.node.nodeRef] === true);
			item.likes = Common.getLikes(node);
			item.location = {};
			item.isInnerAttachment = documentAttachments.isInnerAttachment(item.node);

			// Is our thumbnail type registered?
			if (isThumbnailNameRegistered && item.node.isSubType("cm:content")) {
				// Make sure we have a thumbnail.
				thumbnail = item.node.getThumbnail(THUMBNAIL_NAME);
				if (thumbnail === null) {
					// No thumbnail, so queue creation
					item.node.createThumbnail(THUMBNAIL_NAME, true);
				}
			}

			items.push(item);
		}
	}

	return items;
}

model.items = getAttachmentslist();