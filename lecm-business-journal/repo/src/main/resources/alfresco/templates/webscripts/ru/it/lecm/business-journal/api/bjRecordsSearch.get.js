function main() {
    model.records = businessJournal.getRecordsByParams(args["type"], args["days"], args["whose"]);
}

main();
