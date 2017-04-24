var documentNodeRef = args['documentNodeRef'];
var documentType = args['documentType'];
//model.categories = documentAttachments.getCategories(documentNodeRef);
var items = [];
if (documentNodeRef) {
	var categories = documentAttachments.getCategories(documentNodeRef);
	if (categories) {
		for (var i = 0; i < categories.length; i++) {
			items.push({
				category: categories[i],
				isReadOnly: documentAttachments.isReadonlyCategory(categories[i].nodeRef)
			});
		}
	}
	model.isMlSupported = lecmMessages.isMlSupported();
	model.kind = "node";
} else if (documentType) {
	items = documentAttachments.getCategoriesForType(documentType);
	model.kind = "type";
}

model.items = items;
