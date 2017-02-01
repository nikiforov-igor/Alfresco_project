(function() {
    if (args["items"] && args["busyTimeMembersFields"]) {
        var employees = args["items"].split(",");
        var timeZoneOffset = null;
        if (args['timeZoneOffset']) {
            timeZoneOffset = parseInt(args['timeZoneOffset']);
        }
        var isBusy = false;
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
}());