var results = [];
var total = true;

if (json.has("resolutionExecutionDate") && json.has("errandsExecutionDates")) {
    var resolutionJson = json.get("resolutionExecutionDate");
    var errandsExecutionDatesJson = json.get("errandsExecutionDates");

    var resolutionExecutionDate = edsDocument.convertComplexDateString(
        resolutionJson.has("date-radio") ? resolutionJson.get("date-radio") : null,
        resolutionJson.has("date") ? resolutionJson.get("date") : null,
        resolutionJson.has("date-type") ? resolutionJson.get("date-type") : null,
        resolutionJson.has("date-days") ? resolutionJson.get("date-days") : null);

    var errandsExecutionDates = [];

    for (var i = 0; i < errandsExecutionDatesJson.length(); i++) {
        var errandJson = errandsExecutionDatesJson.get(i);

        var errandExecutionDate = edsDocument.convertComplexDateString(
            errandJson.has("date-radio") ? errandJson.get("date-radio") : null,
            errandJson.has("date") ? errandJson.get("date") : null,
            errandJson.has("date-type") ? errandJson.get("date-type") : null,
            errandJson.has("date-days") ? errandJson.get("date-days") : null);

        var valid = resolutionExecutionDate == null || errandExecutionDate == null || (resolutionExecutionDate.getTime() >= errandExecutionDate.getTime());
        total = total && valid;
        results.push(valid);
    }
}

model.total = total;
model.results = results;