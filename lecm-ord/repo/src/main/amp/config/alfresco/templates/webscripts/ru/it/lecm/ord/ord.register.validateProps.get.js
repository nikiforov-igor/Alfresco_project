var ord = search.findNode(args["nodeRef"]);
if (ord) {
    var ordHaveController = ord.assocs["lecm-ord:controller-assoc"] && ord.assocs["lecm-ord:controller-assoc"].length;
    var ordHaveDueDate = ord.properties["lecm-eds-document:execution-date"] != null;
    var itemsTable = ord.assocs["lecm-ord-table-structure:items-assoc"][0];
    var ordPoints = documentTables.getTableDataRows(itemsTable.nodeRef.toString());
    var havePointWithController = false;
    var havePointWithDueDate = false;
    if (ordPoints && ordPoints.length) {
        for (var i = 0; i < ordPoints.length; i++) {
            if (ordPoints[i].assocs["lecm-ord-table-structure:controller-assoc"] && ordPoints[i].assocs["lecm-ord-table-structure:controller-assoc"].length) {
                havePointWithController = true;
            }
            havePointWithDueDate = ordPoints[i].properties["lecm-ord-table-structure:limitation-date-radio"] != "LIMITLESS";
        }
    } else {
        model.haveNotPoints = true;
    }
    model.haveNotPointsWithController = ordHaveController && !havePointWithController;
    model.haveNotPointsWithDueDate = ordHaveDueDate && !havePointWithDueDate;
    model.haveNotPointsWithControllerAndDueDate = ordHaveController && !havePointWithController && ordHaveDueDate && !havePointWithDueDate;
}
