items = documentTables.getTableDataRows(args["tableDataRef"]);
if (items) {
    model.number = items.length + 1;
}
