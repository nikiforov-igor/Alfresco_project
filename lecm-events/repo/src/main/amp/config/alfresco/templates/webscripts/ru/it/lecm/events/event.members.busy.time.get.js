function main() {
    if (args["items"] && args["date"] && args["busyTimeMembersFields"]) {
        var employees = args["items"].split(",");
        var result = [];
        var additionalFilterFields = args["busyTimeMembersFields"].split(",");
        for each(var employee in employees) {
            if (employee) {
                var additionalFilter = "AND (";
                for (var i=0; i < additionalFilterFields.length; ++i) {
                    var formatedField = additionalFilterFields[i].replace(":","\\:");
                    formatedField = replaceAll("-", "\\-", formatedField);
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
}

function replaceAll(find, replace, str) {
    return str.replace(new RegExp(find, 'g'), replace);
}

main();