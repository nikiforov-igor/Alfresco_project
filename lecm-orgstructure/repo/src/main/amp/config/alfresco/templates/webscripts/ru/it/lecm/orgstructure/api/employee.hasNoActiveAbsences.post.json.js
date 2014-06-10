logger.log("employee.hasNoActiveAbsences!!!!");
logger.log("employee.hasNoActiveAbsences! NodeRef: " + json.get("nodeRef") );
var nodeRef = search.findNode(json.get("nodeRef"));
var cname = nodeRef.properties["cm:name"];
logger.log("employee.hasNoActiveAbsences! Employee CName: " + cname);
model.hasNoActiveAbsences = !absence.isEmployeeAbsentToday(json.get("nodeRef"));
logger.log("employee.hasNoActiveAbsences! model.hasNoActiveAbsences: " + model.hasNoActiveAbsences );
model.reason = cname;
