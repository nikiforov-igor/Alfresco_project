var documentNodeRef = args['documentNodeRef'];
var count = parseInt(args['count']);
var lockStatus = {};

var categories = documentAttachments.getCategories(documentNodeRef);
var items = [];
var hasNext = false;
var k = 0;
if (categories != null) {
	for (var i = 0; i < categories.length; i++) {
		if (k <= count) {
			var attachments = categories[i].getChildren();
			if (attachments != null && attachments.length > 0) {
				var showAttachments = [];
				for (var j = 0; j < attachments.length; j++) {
					if (k < count) {
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
