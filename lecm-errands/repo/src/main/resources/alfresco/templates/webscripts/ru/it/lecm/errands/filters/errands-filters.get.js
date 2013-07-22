function main() {
    var filterId = args.filterId
    var path = documentScript.getDraftsPath();
    var docPath = documentScript.getDocumentsPath();
    var type = "lecm-errands:document";
    model.records = errands.getErrandsDocsByFilter([type], [path,docPath], filterId);
}

main();