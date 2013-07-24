function main() {
    var skipCount = args.skipCount;
    var maxItems = args.maxItems;
    var path = documentScript.getDraftsPath();
    var docPath = documentScript.getDocumentsPath();
    var records = errands.getErrandsDocs([path,docPath],skipCount,maxItems);
    model.records = records;
    model.skipCount = skipCount;
    model.totalItems = records.size();
    model.maxItems = maxItems;
}

main();