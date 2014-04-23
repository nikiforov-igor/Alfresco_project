var request = jsonUtils.toObject(json);

var regNumber = request.regNumber;
var documentNodeRef = request.documentNodeRef;

if (regNumber && documentNodeRef) {
	model.isNumberUnique = regnumbers.isNumberUnique(regNumber, documentNodeRef);
} else if (regNumber && !documentNodeRef) {
	model.isNumberUnique = regnumbers.isNumberUnique(regNumber);
} else {
	status.code = 400;
	status.message = "Insifficient params!";
}
