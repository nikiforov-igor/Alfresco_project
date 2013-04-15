function main() {
    if (args["checkMainObject"] == null) {
        model.records = businessJournal.getRecordsByParams(args["type"], args["days"], args["whose"]);
    } else {
        model.records = businessJournal.getRecordsByParams(args["type"], args["days"], args["whose"], args["checkMainObject"]);
    }
}

main();
