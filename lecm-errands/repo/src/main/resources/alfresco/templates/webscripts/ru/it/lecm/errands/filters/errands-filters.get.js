function main() {
    var path = documentScript.getDraftsPath();
    var docPath = documentScript.getDocumentsPath();
    model.records = errands.getErrandsDocs([path,docPath]);
}

main();