var results = [];
var total = true;

if (json.has("resolutionExecutionDate") && json.has("errandsExecutionDates")) {

    var resolutionExecutionDateJson = json.get("resolutionExecutionDate");
    var errandsExecutionDatesJson = json.get("errandsExecutionDates");

    var resolutionExecutionDate = getExecutionDate(resolutionExecutionDateJson);
    var errandsExecutionDates = [];

    for (var i = 0; i < errandsExecutionDatesJson.length(); i++) {
        var errandExecutionDate = getExecutionDate(errandsExecutionDatesJson.get(i));
        var invalid = resolutionExecutionDate == null || errandExecutionDate == null || (resolutionExecutionDate.getTime() >= errandExecutionDate.getTime())
        total = total && invalid;
        results.push(invalid);
    }
}

function getExecutionDate(json) {
    var result = null;
    if (json.has("date-radio")) {
        if (json.get("date-radio") == "DATE" && json.has("date")) {
            result = utils.fromISO8601(json.get("date"));
        } else if (json.get("date-radio") == "DAYS" && json.has("date-type") && json.has("date-days")) {
            var type = json.get("date-type");
            var days = parseInt(json.get("date-days"));

            result = new Date();
            result.setHours(12);
            result.setMinutes(0);
            result.setSeconds(0);
            result.setMilliseconds(0);

            if (type == "CALENDAR") {
                result.setDate(result.getDate() + days);
            } else if (type == "WORK") {
                result = workCalendar.getNextWorkingDateByDays(result, days)
            }
        }
    }
    return result;
}

model.total = total;
model.results = results;