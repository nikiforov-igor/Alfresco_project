var documentNodeRef = args['documentNodeRef'];
var count = parseInt(args['count']);
var showEmptyCategory = ('' + args['showEmptyCategory']).toLowerCase() == 'true';
var baseDocAssocName = '' + args['baseDocAssocName'];
var lockStatus = {};

var categories = documentAttachments.getCategories(documentNodeRef);
var items = [];
var hasNext = false;
var k = 0;
if (categories) {
	for (var i = 0; i < categories.length; i++) {
		if (k <= count || isNaN(count)) {
			var attachments = documentAttachments.getAttachmentsByCategory(categories[i]);
			if (attachments && (attachments.length || showEmptyCategory)) {
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

	var document = utils.getNodeFromString(documentNodeRef);
	if (document && baseDocAssocName) {
		var baseDoc = null;
		while (document && document.hasPermission("Read")) {
			var baseDocs = document.assocs[baseDocAssocName];
			if (baseDocs && baseDocs.length) {
				baseDoc = baseDocs[0];
				document = baseDocs[0];
			} else {
				document = null;
			}
		}

		if (baseDoc) {
			var categories = documentAttachments.getCategories(baseDoc.nodeRef.toString());
			if (categories) {
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
				if (showAttachments && (showAttachments.length || showEmptyCategory)) {
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
