var fromParent = args["fromParent"] != null && args["fromParent"] == "true";

var report = search.findNode(args["reportId"]);
if (fromParent) {
    report = report.parent;
}
model.results = [];

if (report){
    var assocs = report.assocs["lecm-rpeditor:reportTemplateAssoc"];
    if (assocs && assocs.length > 0){
        model.results = assocs;
    }
}
