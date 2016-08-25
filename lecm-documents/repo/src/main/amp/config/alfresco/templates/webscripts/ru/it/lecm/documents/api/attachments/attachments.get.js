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
						lockStatus[attachment.nodeRef.toString()] = attachment.isLocked;
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

	var document = search.findNode(documentNodeRef);
	if (document != null && document.typeShort == "lecm-errands:document") {
		while (document != null && document.typeShort == "lecm-errands:document" && document.hasPermission("Read")) {
			var baseDocAssoc = document.assocs["lecm-errands:additional-document-assoc"];
			if (baseDocAssoc != null && baseDocAssoc.length > 0) {
				document = baseDocAssoc[0];
			} else {
				document = null;
			}
		}

		if (document != null) {
			var categories = documentAttachments.getCategories(document.nodeRef.toString());
			if (categories != null) {
				var showAttachments = [];
				for (var i = 0; i < categories.length; i++) {
					var attachments = documentAttachments.getAttachmentsByCategory(categories[i]);
					for (var j = 0; j < attachments.length; j++) {
						if (k < count || isNaN(count)) {
							var attachment = attachments[j];
							lockStatus[attachment.nodeRef.toString()] = attachment.isLocked;
							showAttachments.push(attachment);
						} else {
							hasNext = true;
						}
						k++;
					}
				}
				if (showAttachments != null && (showAttachments.length > 0 || (showEmptyCategory != null && showEmptyCategory == "true"))) {
					items.push({
						category: {
							node: {
								nodeRef: "",
								name: "Документ-основание"
							},
							isReadOnly: true
						},
						attachments: showAttachments
					});
				}
			}
		}
	}
}
model.items = items;
model.hasNext = hasNext;
model.lockStatus = lockStatus;
model.isMlSupported = lecmMessages.isMlSupported();
