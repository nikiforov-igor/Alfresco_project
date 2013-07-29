function main() {
    model.bubblingLabel = args["bubblingLabel"];
    model.attributeForShow = args["attributeForShow"];
    model.filterProperty = args["filterProperty"] ? args["filterProperty"] : "lecm-statemachine:status";
    model.formId = args["formId"] ? args["formId"] : "";
    model.query = args["query"];
}

main();
