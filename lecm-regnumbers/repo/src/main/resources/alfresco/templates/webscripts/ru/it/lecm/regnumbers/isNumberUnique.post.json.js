var request = jsonUtils.toObject(json);

var regNumber = request.regNumber;

if (regNumber) {
	model.isNumberUnique = regnumbers.isNumberUnique(regNumber);
} else {
	status.code = 400;
	status.message = "Insifficient params!";
}
