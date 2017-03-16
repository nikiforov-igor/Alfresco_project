/*
	для указанного документа получаем список категорий вложений
	для каждой категории вложения получаем список файликов
	category: {
		nodeRef: ''
		displayName: '',
		attachments: [
			{
				displayName: '',
				nodeRef: '',
				size: ''

			}
		]
	}
*/
(function() {
	var documentNodeRef = args['documentNodeRef'];
	// var document = search.findNode(documentNodeRef);
	var categories = documentAttachments.getCategories(documentNodeRef);
	var i, j, category, attachments, attachment;
	var isMlSupported = lecmMessages.isMlSupported();
	model.categories = [];
	model.shareContext = base.getShareContext();
	if (categories) {
		for (i in categories) {
			category = categories[i];
			model.categories.push({
				nodeRef: category.nodeRef.toString(),
				displayName: isMlSupported && category.properties['cm:title'] ? category.properties['cm:title'] : category.name,
				attachments: []
			});

			attachments = documentAttachments.getAttachmentsByCategory(category);
			if (attachments) {
				for (j in attachments) {
					attachment = attachments[j];
					model.categories[i].attachments.push({
						nodeRef: attachment.nodeRef.toString(),
						displayName: attachment.name,
						size: attachment.size
					});
				}
			}
		}
	}
})();
