function main() {
    model.bubblingLabel = args["bubblingLabel"];
    model.advSearchFormId = args["advSearchFormId"];
    model.itemType = args["itemType"];
    model.attributeForShow = args["attributeForShow"];
    model.excludedColumns = args["excludedColumns"];
    model.filterProperty = args["filterProperty"] ? args["filterProperty"] : "lecm-statemachine:status";
}

main();
