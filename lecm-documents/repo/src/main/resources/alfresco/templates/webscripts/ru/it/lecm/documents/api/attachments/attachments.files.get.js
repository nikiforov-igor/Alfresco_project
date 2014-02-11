var nodeRef = args['nodeRef'];

var categories = documentAttachments.getCategories(nodeRef);
var items = [];

if (categories != null) {
	for (var i = 0; i < categories.length; i++) {
		var attachments = documentAttachments.getAttachmentsByCategory(categories[i]);
		if (attachments != null && attachments.length > 0) {
			for (var j = 0; j < attachments.length; j++) {
				items.push(attachments[j]);
			}
		}
	}
}
model.items = items;