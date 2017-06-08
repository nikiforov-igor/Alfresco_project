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

	var categoryNode = parsedArgs.pathNode;
	if (categoryNode !== null) {
		allNodes = documentAttachments.getAttachmentsByCategory(categoryNode);
	}

	var isThumbnailNameRegistered = thumbnailService.isThumbnailNameRegistered(THUMBNAIL_NAME),
		thumbnail = null,
		item;

	var meta = {
		document: categoryNode ? categoryNode.parent.parent.nodeRef.toString() : "",
		category: categoryNode ? categoryNode.nodeRef.toString() : ""
	};

	model.isComplex = base.getGlobalProperty("lecm.complex.attachment.enable", "false") == "true";

	// Loop through and evaluate each node in this result set
	for each(node in allNodes) {
		// Get evaluated properties.
		item = Evaluator.run(node);
		if (item !== null) {
			item.isFavourite = (favourites[item.node.nodeRef] === true);
			item.likes = Common.getLikes(node);
			item.location = {};
			item.isInnerAttachment = documentAttachments.isInnerAttachment(item.node);
			item.meta = meta;

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