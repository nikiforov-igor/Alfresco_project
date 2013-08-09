var errandNodeRef = args['nodeRef'];

var errand = search.findNode(errandNodeRef);
var items = [];

if (errand) {
    var coexecs = errand.associations["lecm-errands:coexecutors-assoc"];

    if (coexecs != null && coexecs.length > 0) {
        for (var i = 0; i < coexecs.length; i++) {
            var coexec = coexecs[i];
            var coexecNodeRef = coexec.nodeRef;
            var primaryPosition = orgstructure.getPrimaryPosition(coexecNodeRef);
            coexec.properties["employeePosition"] = (primaryPosition != null && primaryPosition.assocs["lecm-orgstr:element-member-position-assoc"] != null) ? primaryPosition.assocs["lecm-orgstr:element-member-position-assoc"][0].getName() : "";
            coexec.properties["employeeFIO"] = coexec.properties["lecm-orgstr:employee-last-name"] + " " + coexec.properties["lecm-orgstr:employee-first-name"] + " " + (coexec.properties["lecm-orgstr:employee-middle-name"]!=null ? coexec.properties["lecm-orgstr:employee-middle-name"] : "");

            items.push({
                "nodeRef": coexecNodeRef.toString(),
                "employeeName": coexec.properties["employeeFIO"],
                "employeePosition": coexec.properties["employeePosition"]
            });
        }
    }
}
model.coexecs = items;
