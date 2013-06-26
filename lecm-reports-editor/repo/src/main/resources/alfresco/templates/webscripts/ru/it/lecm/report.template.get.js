var report = search.findNode(args["reportId"]);
if (report){
    var assocs = report.assocs["lecm-rpeditor:reportTemplateAssoc"];
    if (assocs && assocs.length > 0){
        model.template = assocs[0];
    }
}
