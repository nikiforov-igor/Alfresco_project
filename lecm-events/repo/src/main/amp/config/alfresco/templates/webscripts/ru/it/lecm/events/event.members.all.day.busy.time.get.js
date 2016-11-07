if (args["items"]) {
    var employees = args["items"].split(",");
    var timeZoneOffset = null;
    if (args['timeZoneOffset']) {
        timeZoneOffset = parseInt(args['timeZoneOffset']);
    }
    var isBusy = false;
    for each(var employee in employees) {
        if (employee) {
            var additionalFilter = "AND (@lecm\\-events\\:initiator\\-assoc\\-ref: \"*" + employee + "*\" OR ";
            additionalFilter += "@lecm\\-events\\:temp\\-members\\-assoc\\-ref: \"*" + employee + "*\" OR ";
            additionalFilter += "@lecm\\-meetings\\:chairman\\-assoc\\-ref: \"*" + employee + "*\" OR ";
            additionalFilter += "@lecm\\-meetings\\:secretary\\-assoc\\-ref: \"*" + employee + "*\")";
            var eventsCollection = events.getUserEvents(args["startDate"], args["endDate"], additionalFilter, true);

            for (var i = 0; i < eventsCollection.size(); i++) {
                var event = eventsCollection.get(i);
                if (("" + event["nodeRef"]) != ("" + args["exclude"])) {
                    isBusy = true;
                    break;
                }
            }
        }
        if (isBusy) {
            break;
        }
    }
    var result = {
        isBusy: isBusy
    };

    model.result = jsonUtils.toJSONString(result);
}