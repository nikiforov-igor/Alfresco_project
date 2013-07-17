function main() {
    if (args["checkMainObject"] == null) {
        model.records = businessJournal.getRecordsByParams(args["type"], args["days"], args["whose"]);
    } else if (args["skipCount"] == null || args["maxItems"] == null){
        model.records = businessJournal.getRecordsByParams(args["type"], args["days"], args["whose"], args["checkMainObject"]);
    } else {
        model.records = businessJournal.getRecordsByParams(args["type"], args["days"], args["whose"], args["checkMainObject"], args["skipCount"], args["maxItems"]);
    }
}

main();
