var request = jsonUtils.toObject(json);

var documentNodeRef = request.documentNodeRef;
var template = request.template;

if (documentNodeRef && template) {
	model.regNumber = regnumbers.getNumber(search.findNode(documentNodeRef), template);
} else {
	status.code = 400;
	status.message = "Insifficient params!";
}
