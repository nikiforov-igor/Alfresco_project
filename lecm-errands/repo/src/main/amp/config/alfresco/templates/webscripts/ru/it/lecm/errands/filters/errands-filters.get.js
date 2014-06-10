function main() {
    var skipCount = args.skipCount;
    var maxItems = args.maxItems;
    var path = documentScript.getDraftsPath();
    var docPath = documentScript.getDocumentsPath();
    var records = errands.getErrandsDocs([path,docPath],skipCount,maxItems);
    model.records = [];
    for each (var record in records) {

        var baseDocString = null;
        if (record.assocs["lecm-errands:additional-document-assoc"] != null) {
            baseDocString = documentScript.getPresentString(record.assocs["lecm-errands:additional-document-assoc"][0]);
        }

        model.records.push(    {
            "nodeRef": record.getNodeRef().toString(),
            "record": record.properties["lecm-document:present-string"],
            "date": record.properties["lecm-errands:limitation-date"],
            "baseDocString": baseDocString,
            "title": record.properties["lecm-errands:title"],
            "number":   record.properties["lecm-errands:number"].toString(),
            "initiator": record.assocs["lecm-errands:initiator-assoc"][0].nodeRef.toString(),
            "initiator_name": record.properties["lecm-errands:initiator-assoc-text-content"],
            "isExpired": record.properties["lecm-errands:is-expired"],
            "isImportant": record.properties["lecm-errands:is-important"]
        });
    }
    model.skipCount = skipCount;
    model.totalItems = records.length;
    model.maxItems = maxItems;
}

main();