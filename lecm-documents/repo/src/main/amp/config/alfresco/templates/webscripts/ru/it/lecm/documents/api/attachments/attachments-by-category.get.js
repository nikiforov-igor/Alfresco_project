var documentNodeRef = args['documentNodeRef'];
var category = args['category'];

model.attachments = documentAttachments.getAttachmentsByCategory(documentNodeRef, category);