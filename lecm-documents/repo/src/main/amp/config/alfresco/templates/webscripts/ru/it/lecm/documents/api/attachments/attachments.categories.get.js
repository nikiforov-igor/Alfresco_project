var documentNodeRef = args['documentNodeRef'];

//model.categories = documentAttachments.getCategories(documentNodeRef);

var categories = documentAttachments.getCategories(documentNodeRef);
var items = [];
if (categories != null) {
	for (var i = 0; i < categories.length; i++) {
		items.push({
			category: categories[i],
			isReadOnly: documentAttachments.isReadonlyCategory(categories[i].nodeRef)
		});
	}
}
model.isMlSupported = lecmMessages.isMlSupported();
model.items = items;
