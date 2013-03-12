var documentNodeRef = args['documentNodeRef'];
var count = parseInt(args['count']);

var categories = documentAttachments.getCategories(documentNodeRef);
var items = [];
var k = 0;
if (categories != null) {
	for (var i = 0; i < categories.length; i++) {
		if (k < count) {
			var attachments = categories[i].getChildren();
			if (attachments != null && attachments.length > 0) {
				var showAttachments = [];
				for (var j = 0; j < attachments.length; j++) {
					if (k < count) {
						showAttachments.push(attachments[j]);
						k++;
					}
				}

				items.push({
					category: categories[i],
					attachments: showAttachments
				});
			}
		}
	}
}
model.items = items;