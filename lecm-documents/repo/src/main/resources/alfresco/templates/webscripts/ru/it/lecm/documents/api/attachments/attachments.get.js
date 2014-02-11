var documentNodeRef = args['documentNodeRef'];
var count = parseInt(args['count']);
var showEmptyCategory = args['showEmptyCategory'];
var lockStatus = {};

var categories = documentAttachments.getCategories(documentNodeRef);
var items = [];
var hasNext = false;
var k = 0;
if (categories != null) {
	for (var i = 0; i < categories.length; i++) {
		if (k <= count || isNaN(count)) {
			var attachments = documentAttachments.getAttachmentsByCategory(categories[i]);
			if (attachments != null && (attachments.length > 0 || (showEmptyCategory != null && showEmptyCategory == "true"))) {
				var showAttachments = [];
				for (var j = 0; j < attachments.length; j++) {
					if (k < count || isNaN(count)) {
						var attachment = attachments[j];
						var aspects = attachment.aspects;
						var locked = false;
						if (aspects) {
							for (var l = 0; l < aspects.length; l++) {
								if (aspects[l] == "{http://www.alfresco.org/model/content/1.0}lockable") {
									locked = true;
									break;
								}
							}
						}
						lockStatus[attachment.nodeRef.toString()] = locked;
						showAttachments.push(attachment);
					} else {
						hasNext = true;
					}
					k++;
				}

				items.push({
					category: {
						node: categories[i],
						isReadOnly: documentAttachments.isReadonlyCategory(categories[i].nodeRef)
					},
					attachments: showAttachments
				});
			}
		}
	}
}
model.items = items;
model.hasNext = hasNext;
model.lockStatus = lockStatus;
