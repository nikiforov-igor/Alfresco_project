var employee = orgstructure.getCurrentEmployee();
var maxItems = 5;
try {
    maxItems = parseInt(args['maxItems']);
} catch (e) {}
var nearestEvents = events.getUserNearestEvents(employee, maxItems);

var result = [];

for each (var ev in nearestEvents) {
    result.push({
        nodeRef: ev.nodeRef.toString(),
        title: ev.properties['lecm-events:title'],
        fromDate: ev.properties['lecm-events:from-date'].getTime(),
        toDate: ev.properties['lecm-events:to-date'].getTime(),
        location: ev.assocs['lecm-events:location-assoc'][0].properties['cm:name'],
        allDay: ev.properties['lecm-events:all-day']
    });
}

model.result = jsonUtils.toJSONString(result);