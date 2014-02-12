var query = 'TYPE:"lecm-document:base"';
var allDocuments = search.luceneSearch(query);
var updatedCategories = 0;
var createdAssociations = 0;
if (allDocuments != null) {
	for (var i = 0; i < allDocuments.length; i++) {
		var document = allDocuments[i];

		var categories = documentAttachments.getCategories(document.nodeRef);
		if (categories != null) {
			for (var j = 0; j < categories.length; j++) {
				var category = categories[j];

				if (category.typeShort != "lecm-document:attachmentsCategory") {
					category.specializeType("lecm-document:attachmentsCategory");
					updatedCategories++;
				}
				var attachments = categories[j].getChildren();

				if (attachments != null) {
					for (var k = 0; k < attachments.length; k++) {
						var attachment = attachments[k];

						var assocExist = false;
						var existAssociations = category.assocs["lecm-document:categoryAttachments"];
						if (existAssociations != null) {
							for (var ii = 0; ii < existAssociations.length; ii++) {
								if (existAssociations[ii].nodeRef.equals(attachment.nodeRef)) {
									assocExist = true;
								}
							}
						}

						if (!assocExist) {
							category.createAssociation(attachment, "lecm-document:categoryAttachments");
							createdAssociations++;
						}
					}
				}
			}
		}
	}
}
model.updatedCategories = updatedCategories;
model.createdAssociations = createdAssociations;
