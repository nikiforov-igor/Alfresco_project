function main() {
    model.bubblingLabel = args["bubblingLabel"];
    model.itemType = args["itemType"];
    model.attributeForShow = args["attributeForShow"];
    model.excludedColumns = args["excludedColumns"];
    model.nowrapColumns = args["nowrapColumns"];
    model.filterProperty = args["filterProperty"] ? args["filterProperty"] : "lecm-statemachine:status";
    model.formId = args["formId"] ? args["formId"] : "";
    model.query = args["query"];
    model.includedStatuses = args["includedStatuses"];
}

main();
