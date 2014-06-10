var nodeRef = args['nodeRef'];

var category = documentAttachments.getCategoryByAttachment(nodeRef);

if (category != null) {
	model.category = {
		node: category,
		isReadOnly: documentAttachments.isReadonlyCategory(category.nodeRef)
	};
} else {
	model.category = null;
}