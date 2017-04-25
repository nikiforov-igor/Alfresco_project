(function() {
    if (args["items"] && args["date"] && args["busyTimeMembersFields"]) {
        var employees = args["items"].split(",");
        var result = [];
        var additionalFilterFields = args["busyTimeMembersFields"].split(",");
        for each(var employee in employees) {
            if (employee) {
                var additionalFilter = "AND (";
                for (var i=0; i < additionalFilterFields.length; ++i) {
                    var formatedField = additionalFilterFields[i].replace(/:/g, "\\:").replace(/-/g, "\\-");
                    formatedField += "\\-ref";
                    additionalFilter += ("@" + formatedField + ": \"*" + employee + "*\"");
                    if (i < additionalFilterFields.length - 1) {
                        additionalFilter += " OR ";
                    }
                }
                additionalFilter += ")";

                var eventsCollection = events.getUserEvents(args["date"] + "T00:00:00Z", args["date"] + "T23:59:59Z", additionalFilter, true);
                var busytime = [];
                for (var i = 0; i < eventsCollection.size(); i++) {
                    var event = eventsCollection.get(i);
                    if (("" + event["nodeRef"]) != ("" + args["exclude"])) {
                        busytime.push({
                            title: event["title"],
                            start: event["start"],
                            end: event["end"],
                            startDate: utils.toISO8601(event["startDate"]),
                            endDate: utils.toISO8601(event["endDate"])
                        })
                    }
                }
                result.push({
                    employee: employee,
                    busytime: busytime
                })
            }
        }
		var ewsCollection = ews.getEvents(employees, args["date"] + "T00:00:00Z", args["nextDate"] + "T00:00:00Z");
        model.result = jsonUtils.toJSONString(result.concat(ewsCollection));
    }
}());
