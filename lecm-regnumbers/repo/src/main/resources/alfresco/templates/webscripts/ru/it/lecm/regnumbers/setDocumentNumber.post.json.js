var request = jsonUtils.toObject(json);

var documentNodeRef = request.documentNodeRef;
var documentProperty = request.documentProperty;
var template = request.template;

if (documentNodeRef && documentProperty && template) {
	regnumbers.setDocumentNumber(search.findNode(documentNodeRef), documentProperty, template);
	model.success = true;
} else {
	status.code = 400;
	status.message = "Insifficient params!";
	model.success = false;
}
