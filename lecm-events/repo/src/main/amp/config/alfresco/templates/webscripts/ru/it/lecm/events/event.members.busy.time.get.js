if (args["items"] && args["date"]) {
    var employees = args["items"].split(",");
    var result = [];
    for each(var employee in employees) {
        if (employee) {
            var additionalFilter = "AND (@lecm\\-events\\:initiator\\-assoc\\-ref: \"*" + employee + "*\" OR ";
            additionalFilter += "@lecm\\-events\\:temp\\-members\\-assoc\\-ref: \"*" + employee + "*\" OR ";
            additionalFilter += "@lecm\\-meetings\\:chairman\\-assoc\\-ref: \"*" + employee + "*\" OR ";
            additionalFilter += "@lecm\\-meetings\\:secretary\\-assoc\\-ref: \"*" + employee + "*\")";
            var eventsCollection = events.getUserEvents(args["date"] + "T00:00:00Z", args["date"] + "T23:59:59Z", additionalFilter, true);
            var busytime = [];
            for (var i = 0; i < eventsCollection.size(); i++) {
                var event = eventsCollection.get(i);
                if (("" + event["nodeRef"]) != ("" + args["exclude"])) {
                    busytime.push({
                        title: event["title"],
                        start: event["start"],
                        end: event["end"]
                    })
                }
            }
            result.push({
                employee: employee,
                busytime: busytime
            })
        }
    }
    model.result = jsonUtils.toJSONString(result);
}