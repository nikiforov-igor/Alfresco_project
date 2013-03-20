var documentNodeRef = args['documentNodeRef'];
var template = args['template'];

if (documentNodeRef && template) {
	model.regNumber = regnumbers.getNumber(search.findNode(documentNodeRef), template);
} else {
	status.code = 400;
	status.message = "Insifficient params!";
}
